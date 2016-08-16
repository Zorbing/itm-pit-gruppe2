package de.uniluebeck.itm.pit.ncoap;

import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.log4j.Logger;

import com.google.common.util.concurrent.SettableFuture;

import de.uniluebeck.itm.ncoap.application.server.webservice.NotObservableWebservice;
import de.uniluebeck.itm.ncoap.application.server.webservice.WrappedResourceStatus;
import de.uniluebeck.itm.ncoap.application.server.webservice.linkformat.LongLinkAttribute;
import de.uniluebeck.itm.ncoap.message.CoapMessage;
import de.uniluebeck.itm.ncoap.message.CoapRequest;
import de.uniluebeck.itm.ncoap.message.CoapResponse;
import de.uniluebeck.itm.ncoap.message.MessageCode;
import de.uniluebeck.itm.ncoap.message.options.ContentFormat;

public abstract class AbstractNotObservableWebservice<T> extends NotObservableWebservice<T>
{
	public static long DEFAULT_CONTENT_FORMAT = ContentFormat.TEXT_PLAIN_UTF8;
	
	protected static Logger log = Logger.getLogger(Prefix.class.getName());
	
	public AbstractNotObservableWebservice(String servicePath, T initialStatus, long lifetimeSeconds,
			ScheduledExecutorService executor)
	{
		super(servicePath, initialStatus, lifetimeSeconds, executor);
		
		this.setLinkAttribute(new LongLinkAttribute(LongLinkAttribute.CONTENT_TYPE, ContentFormat.TEXT_PLAIN_UTF8));
		this.setLinkAttribute(new LongLinkAttribute(LongLinkAttribute.CONTENT_TYPE, ContentFormat.APP_XML));
		this.setLinkAttribute(new LongLinkAttribute(LongLinkAttribute.CONTENT_TYPE, ContentFormat.APP_TURTLE));
	}
	
	public void processCoapGetRequest(SettableFuture<CoapResponse> responseFuture, CoapRequest coapRequest)
	{
		// create resource status
		WrappedResourceStatus resourceStatus;
		if (coapRequest.getAcceptedContentFormats().isEmpty())
		{
			resourceStatus = getWrappedResourceStatus(DEFAULT_CONTENT_FORMAT);
		}
		else
		{
			resourceStatus = getWrappedResourceStatus(coapRequest.getAcceptedContentFormats());
		}
		
		// create CoAP response
		CoapResponse coapResponse;
		if (resourceStatus == null)
		{
			coapResponse = new CoapResponse(coapRequest.getMessageTypeName(), MessageCode.Name.NOT_ACCEPTABLE_406);
			coapResponse.setContent("None of the accepted content formats is supported!".getBytes(CoapMessage.CHARSET),
					ContentFormat.TEXT_PLAIN_UTF8);
		}
		
		else
		{
			coapResponse = new CoapResponse(coapRequest.getMessageTypeName(), MessageCode.Name.CONTENT_205);
			coapResponse.setContent(resourceStatus.getContent(), resourceStatus.getContentFormat());
			coapResponse.setEtag(resourceStatus.getEtag());
			coapResponse.setMaxAge(resourceStatus.getMaxAge());
		}
		
		responseFuture.set(coapResponse);
	}
	
	@Override
	public void processCoapRequest(SettableFuture<CoapResponse> responseFuture, CoapRequest coapRequest,
			InetSocketAddress remoteEndpoint) throws Exception
	{
		if (coapRequest.getMessageCodeName() != MessageCode.Name.GET)
		{
			setMethodNotAllowedResponse(responseFuture, coapRequest);
		}
		else
		{
			processCoapGetRequest(responseFuture, coapRequest);
		}
		
	}
	
	public void setMethodNotAllowedResponse(SettableFuture<CoapResponse> responseFuture, CoapRequest coapRequest)
			throws Exception
	{
		CoapResponse coapResponse = new CoapResponse(coapRequest.getMessageTypeName(),
				MessageCode.Name.METHOD_NOT_ALLOWED_405);
		
		coapResponse.setContent("Only method GET is allowed!".getBytes(CoapMessage.CHARSET),
				ContentFormat.TEXT_PLAIN_UTF8);
		
		responseFuture.set(coapResponse);
	}
	
	@Override
	public void shutdown()
	{
	}
}
