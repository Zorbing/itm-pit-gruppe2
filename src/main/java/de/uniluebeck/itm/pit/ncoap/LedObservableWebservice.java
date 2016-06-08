package de.uniluebeck.itm.pit.ncoap;

import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.log4j.Logger;

import com.google.common.util.concurrent.SettableFuture;

import de.uniluebeck.itm.ncoap.application.server.webservice.ObservableWebservice;
import de.uniluebeck.itm.ncoap.application.server.webservice.WrappedResourceStatus;
import de.uniluebeck.itm.ncoap.application.server.webservice.linkformat.LongLinkAttribute;
import de.uniluebeck.itm.ncoap.application.server.webservice.linkformat.StringLinkAttribute;
import de.uniluebeck.itm.ncoap.communication.dispatching.client.Token;
import de.uniluebeck.itm.ncoap.message.CoapMessage;
import de.uniluebeck.itm.ncoap.message.CoapRequest;
import de.uniluebeck.itm.ncoap.message.CoapResponse;
import de.uniluebeck.itm.ncoap.message.MessageCode;
import de.uniluebeck.itm.ncoap.message.options.ContentFormat;

public class LedObservableWebservice extends ObservableWebservice<Boolean> {
	
	public static long DEFAULT_CONTENT_FORMAT = ContentFormat.TEXT_PLAIN_UTF8;
	private static Logger log = Logger.getLogger(LedObservableWebservice.class.getName());
	
	public static String groupNr = "02";
	public static String prefix = "pit: <http://gruppe" + groupNr + ".pit.itm.uni-luebeck.de/>";
//	public static String prefix = "pit: <http://www.itm.uni-luebeck.de/ontologies/pit2016/>";
	public static HashMap<Long, String> payloadTemplates = new HashMap<>();
	static{
		//Add template for plaintext UTF-8 payload
		payloadTemplates.put(
			ContentFormat.TEXT_PLAIN_UTF8,
			"The LED is currently on: %B"
		);

		//Add template for XML payload
		payloadTemplates.put(
			ContentFormat.APP_XML,
			"<state>%b</time>"
		);
		
		//Add template for Turtle payload
		payloadTemplates.put(
			ContentFormat.APP_TURTLE,
			"@prefix " + prefix + "\n" +
			"@prefix xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
			"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"\n" + 
			"pit:Gruppe" + groupNr + "_Pi rdf:type pit:Hardware .\n" +
			"pit:Gruppe" + groupNr + "_Pi pit:hasPart pit:Gruppe" + groupNr + "_LDR .\n" +
			"pit:Gruppe" + groupNr + "_Pi pit:isLocatedIn pit:Room2054 .\n" +
			"\n" +
			"pit:Gruppe" + groupNr + "_LDR rdf:type pit:GL5516_LDR .\n" +
			"pit:Gruppe" + groupNr + "_LDR pit:hasSensor pit:Gruppe" + groupNr + "_LDR_Sensor .\n" +
			"\n" +
			"pit:Gruppe" + groupNr + "_LDR_Sensor rdf:type pit:LightSensorBinary .\n" +
			"pit:Gruppe" + groupNr + "_LDR_Sensor pit:observesPhenomenon pit:Room2054 .\n" +
			"\n" +
			"pit:Room2054 rdf:type pit:Phenomenon .\n" +
			"pit:Room2054 pit:isFeataureOf pit:Building64 .\n" +
			"\n" +
			"pit:Gruppe" + groupNr + "_Obs1 rdf:type pit:Observation .\n" +
			"pit:Gruppe" + groupNr + "_Obs1 pit:isStatusOf pit:Gruppe" + groupNr + "_LDR_Sensor .\n" +
			"pit:Gruppe" + groupNr + "_Obs1 pit:hasTimeStamp \"%s\"^^xsd:dateTime .\n" +
			"pit:Gruppe" + groupNr + "_Obs1 pit:hasValue pit:Gruppe" + groupNr + "_Obs1_Value .\n" +
			"\n" +
			"pit:Gruppe" + groupNr + "_Obs1_Value rdf:type pit:typeObservationValue .\n" +
			"pit:Gruppe" + groupNr + "_Obs1_Value pit:hasType pit:Boolean .\n" +
			"pit:Gruppe" + groupNr + "_Obs1_Value pit:literalValue \"%b\"^^xsd:boolean ."
		);
	}

	public LedObservableWebservice(String uriPath, ScheduledExecutorService executor) {
		super(uriPath, false, executor);
		
		//Sets the link attributes for supported content types ('ct')
		this.setLinkAttribute(new LongLinkAttribute(LongLinkAttribute.CONTENT_TYPE, ContentFormat.TEXT_PLAIN_UTF8));
		this.setLinkAttribute(new LongLinkAttribute(LongLinkAttribute.CONTENT_TYPE, ContentFormat.APP_XML));
		this.setLinkAttribute(new LongLinkAttribute(LongLinkAttribute.CONTENT_TYPE, ContentFormat.APP_TURTLE));

		//Sets the link attribute for the resource type ('rt')
		String attributeValue = "The state of the LED";
		this.setLinkAttribute(new StringLinkAttribute(StringLinkAttribute.RESOURCE_TYPE, attributeValue));

		//Sets the link attribute for max-size estimation ('sz')
		this.setLinkAttribute(new LongLinkAttribute(LongLinkAttribute.MAX_SIZE_ESTIMATE, 1L));

		//Sets the link attribute for interface description ('if')
		this.setLinkAttribute(new StringLinkAttribute(StringLinkAttribute.INTERFACE, "CoAP GET"));
	}

	@Override
	public boolean isUpdateNotificationConfirmable(InetSocketAddress remoteEndpoint, Token token) {
		return false;
	}
	
	public byte[] getEtag(long contentFormat) {
		return new byte[] { (byte) (getStatus() ? 1 : 0) };
	}
	
	public void updateEtag(Boolean arg0) {
		//nothing to do here as the ETAG is constructed on demand in the getEtag(long contentFormat) method
	}
	
//	@Override
	public void processCoapRequest(SettableFuture<CoapResponse> responseFuture, CoapRequest coapRequest,
								   InetSocketAddress remoteAddress) {
		try{
			if(coapRequest.getMessageCodeName() == MessageCode.Name.GET){
				processGet(responseFuture, coapRequest);
			}

			else {
				CoapResponse coapResponse = new CoapResponse(coapRequest.getMessageTypeName(),
						MessageCode.Name.METHOD_NOT_ALLOWED_405);
				String message = "Service does not allow " + coapRequest.getMessageCodeName() + " requests.";
				coapResponse.setContent(message.getBytes(CoapMessage.CHARSET), ContentFormat.TEXT_PLAIN_UTF8);
				responseFuture.set(coapResponse);
			}
		}
		catch(Exception ex){
			responseFuture.setException(ex);
		}
	}
	
	private void processGet(SettableFuture<CoapResponse> responseFuture, CoapRequest coapRequest)
			throws Exception {

		//Retrieve the accepted content formats from the request
		Set<Long> contentFormats = coapRequest.getAcceptedContentFormats();

		//If accept option is not set in the request, use the default (TEXT_PLAIN_UTF8)
		if(contentFormats.isEmpty())
			contentFormats.add(DEFAULT_CONTENT_FORMAT);

		//Generate the payload of the response (depends on the accepted content formats, resp. the default
		WrappedResourceStatus resourceStatus = null;
		Iterator<Long> iterator = contentFormats.iterator();
		long contentFormat = DEFAULT_CONTENT_FORMAT;

		while(resourceStatus == null && iterator.hasNext()){
			contentFormat = iterator.next();
			resourceStatus = getWrappedResourceStatus(contentFormat);
		}

		//generate the CoAP response
		CoapResponse coapResponse;

		//if the payload could be generated, i.e. at least one of the accepted content formats (according to the
		//requests accept option(s)) is offered by the Webservice then set payload and content format option
		//accordingly
		if(resourceStatus != null){
			coapResponse = new CoapResponse(coapRequest.getMessageTypeName(), MessageCode.Name.CONTENT_205);
			coapResponse.setContent(resourceStatus.getContent(), contentFormat);

			coapResponse.setEtag(resourceStatus.getEtag());
			coapResponse.setMaxAge(resourceStatus.getMaxAge());

			if(coapRequest.getObserve() == 0)
				coapResponse.setObserve();
		}

		//if no payload could be generated, i.e. none of the accepted content formats (according to the
		//requests accept option(s)) is offered by the Webservice then set the code of the response to
		//400 BAD REQUEST and set a payload with a proper explanation
		else{
			coapResponse = new CoapResponse(coapRequest.getMessageTypeName(), MessageCode.Name.NOT_ACCEPTABLE_406);

			StringBuilder payload = new StringBuilder();
			payload.append("Requested content format(s) (from requests ACCEPT option) not available: ");
			for(long acceptedContentFormat : coapRequest.getAcceptedContentFormats())
				payload.append("[").append(acceptedContentFormat).append("]");

			coapResponse.setContent(payload.toString()
					.getBytes(CoapMessage.CHARSET), ContentFormat.TEXT_PLAIN_UTF8);
		}

		//Set the response future with the previously generated CoAP response
		responseFuture.set(coapResponse);

	}
	
	public byte[] getSerializedResourceStatus(long contentFormat) {
		log.debug("Try to create payload (content format: " + contentFormat + ")");
		
		DateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String timestamp = simpleDateFormat.format(new Date());
		boolean state = getStatus();
		String template = payloadTemplates.get(contentFormat);

		if(template == null)
			return null;

		else
			return String.format(template, timestamp, state).getBytes(CoapMessage.CHARSET);
	}
}
