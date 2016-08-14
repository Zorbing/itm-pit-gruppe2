package de.uniluebeck.itm.pit.ncoap;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.log4j.Logger;

import com.google.common.util.concurrent.SettableFuture;

import de.uniluebeck.itm.ncoap.application.server.webservice.ObservableWebservice;
import de.uniluebeck.itm.ncoap.application.server.webservice.WrappedResourceStatus;
import de.uniluebeck.itm.ncoap.application.server.webservice.linkformat.LongLinkAttribute;
import de.uniluebeck.itm.ncoap.application.server.webservice.linkformat.StringLinkAttribute;
import de.uniluebeck.itm.ncoap.message.CoapMessage;
import de.uniluebeck.itm.ncoap.message.CoapRequest;
import de.uniluebeck.itm.ncoap.message.CoapResponse;
import de.uniluebeck.itm.ncoap.message.MessageCode;
import de.uniluebeck.itm.ncoap.message.options.ContentFormat;

public abstract class AbstractObservableWebservice<T> extends ObservableWebservice<T>
{
	public static long DEFAULT_CONTENT_FORMAT = ContentFormat.TEXT_PLAIN_UTF8;
	
	protected static Logger log = Logger.getLogger(LedWebservice.class.getName());
	
	public AbstractObservableWebservice(String uriPath, T initalStatus, ScheduledExecutorService executor,
		String attributeValue, long maxSize)
	{
		super(uriPath, initalStatus, executor);
		
		// Sets the link attributes for supported content types ('ct')
		this.setLinkAttribute(new LongLinkAttribute(LongLinkAttribute.CONTENT_TYPE, ContentFormat.TEXT_PLAIN_UTF8));
		this.setLinkAttribute(new LongLinkAttribute(LongLinkAttribute.CONTENT_TYPE, ContentFormat.APP_XML));
		this.setLinkAttribute(new LongLinkAttribute(LongLinkAttribute.CONTENT_TYPE, ContentFormat.APP_TURTLE));
		
		// Sets the link attribute for the resource type ('rt')
		this.setLinkAttribute(new StringLinkAttribute(StringLinkAttribute.RESOURCE_TYPE, attributeValue));
		
		// Sets the link attribute for max-size estimation ('sz')
		this.setLinkAttribute(new LongLinkAttribute(LongLinkAttribute.MAX_SIZE_ESTIMATE, maxSize));
		
		// Sets the link attribute for interface description ('if')
		this.setLinkAttribute(new StringLinkAttribute(StringLinkAttribute.INTERFACE, "CoAP GET"));
	}
	
	public abstract byte[] getSerializedResourceStatus(long contentFormat);
	
	// @Override
	public void processCoapRequest(SettableFuture<CoapResponse> responseFuture, CoapRequest coapRequest,
		InetSocketAddress remoteAddress)
	{
		try
		{
			if (coapRequest.getMessageCodeName() == MessageCode.Name.GET)
			{
				processGet(responseFuture, coapRequest);
			}
			
			else
			{
				CoapResponse coapResponse = new CoapResponse(coapRequest.getMessageTypeName(),
					MessageCode.Name.METHOD_NOT_ALLOWED_405);
				String message = "Service does not allow " + coapRequest.getMessageCodeName() + " requests.";
				coapResponse.setContent(message.getBytes(CoapMessage.CHARSET), ContentFormat.TEXT_PLAIN_UTF8);
				responseFuture.set(coapResponse);
			}
		}
		catch (Exception ex)
		{
			responseFuture.setException(ex);
		}
	}
	
	public void updateEtag(Boolean arg0)
	{
		// nothing to do here as the ETAG is constructed on demand in the
		// getEtag(long contentFormat) method
	}
	
	private void processGet(SettableFuture<CoapResponse> responseFuture, CoapRequest coapRequest)
		throws Exception
	{
		
		// Retrieve the accepted content formats from the request
		Set<Long> contentFormats = coapRequest.getAcceptedContentFormats();
		
		// If accept option is not set in the request, use the default
		// (TEXT_PLAIN_UTF8)
		if (contentFormats.isEmpty())
		{
			contentFormats.add(DEFAULT_CONTENT_FORMAT);
		}
		
		// Generate the payload of the response (depends on the accepted content
		// formats, resp. the default
		WrappedResourceStatus resourceStatus = null;
		Iterator<Long> iterator = contentFormats.iterator();
		long contentFormat = DEFAULT_CONTENT_FORMAT;
		
		while (resourceStatus == null && iterator.hasNext())
		{
			contentFormat = iterator.next();
			resourceStatus = getWrappedResourceStatus(contentFormat);
		}
		
		// generate the CoAP response
		CoapResponse coapResponse;
		
		// if the payload could be generated, i.e. at least one of the accepted
		// content formats (according to the
		// requests accept option(s)) is offered by the Webservice then set
		// payload and content format option
		// accordingly
		if (resourceStatus != null)
		{
			coapResponse = new CoapResponse(coapRequest.getMessageTypeName(), MessageCode.Name.CONTENT_205);
			coapResponse.setContent(resourceStatus.getContent(), contentFormat);
			
			coapResponse.setEtag(resourceStatus.getEtag());
			coapResponse.setMaxAge(resourceStatus.getMaxAge());
			
			if (coapRequest.getObserve() == 0)
			{
				coapResponse.setObserve();
			}
		}
		
		// if no payload could be generated, i.e. none of the accepted content
		// formats (according to the
		// requests accept option(s)) is offered by the Webservice then set the
		// code of the response to
		// 400 BAD REQUEST and set a payload with a proper explanation
		else
		{
			coapResponse = new CoapResponse(coapRequest.getMessageTypeName(), MessageCode.Name.NOT_ACCEPTABLE_406);
			
			StringBuilder payload = new StringBuilder();
			payload.append("Requested content format(s) (from requests ACCEPT option) not available: ");
			for (long acceptedContentFormat : coapRequest.getAcceptedContentFormats())
			{
				payload.append("[").append(acceptedContentFormat).append("]");
			}
			
			coapResponse.setContent(payload.toString()
				.getBytes(CoapMessage.CHARSET), ContentFormat.TEXT_PLAIN_UTF8);
		}
		
		// Set the response future with the previously generated CoAP response
		responseFuture.set(coapResponse);
		
	}
}
