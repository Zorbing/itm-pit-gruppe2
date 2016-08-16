package de.uniluebeck.itm.pit.ncoap;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import de.uniluebeck.itm.ncoap.communication.dispatching.client.Token;
import de.uniluebeck.itm.ncoap.message.CoapMessage;
import de.uniluebeck.itm.ncoap.message.options.ContentFormat;

public class AudioPassThroughWebservice extends AbstractObservableWebservice<Boolean>
{
	public static HashMap<Long, String> payloadTemplates = new HashMap<>();
	
	static
	{
		// Add template for plaintext UTF-8 payload
		payloadTemplates.put(
			ContentFormat.TEXT_PLAIN_UTF8,
			"The audio pass through is currently (%s) on: %b");
		
		// Add template for XML payload
		payloadTemplates.put(
			ContentFormat.APP_XML,
			"<audio-pass-through>\n" +
					"<time>%s</time>\n" +
					"<state>%b</state>\n" +
					"</audio-pass-through>");
		
		// Add template for Turtle payload
		payloadTemplates.put(
			ContentFormat.APP_TURTLE,
			Prefix.sparql +
				"\n" +
				Prefix.group + "_Pi rdf:type pit:Hardware .\n" +
				Prefix.group + "_Pi pit:hasPart " + Prefix.group + "_APT .\n" +
				Prefix.group + "_Pi pit:isLocatedIn pit:Room2054 .\n" +
				"\n" +
				Prefix.group + "_APT rdf:type pit:Hardware .\n" +
				Prefix.group + "_APT pit:hasActuator " + Prefix.group + "_APT_Actuator .\n" +
				"\n" +
				// TODO:
				Prefix.group + "_APT_Actuator rdf:type pit:Actuator .\n" +
				Prefix.group + "_APT_Actuator pit:actsOnOperation pit:enableSpeaker .\n" +
				"\n" +
				"pit:enableSpeaker rdf:type pit:PulseOperation .\n" +
				"\n" +
				// static information which will be provided by the SSP
				// "pit:Room2054 rdf:type pit:Phenomenon .\n" +
				// "pit:Room2054 pit:isFeatureOf pit:Building64 .\n" +
				// "\n" +
				Prefix.group + "_Comm1 rdf:type pit:Command .\n" +
				Prefix.group + "_Comm1 pit:isCommandTo " + Prefix.group + "_APT_Actuator .\n" +
				Prefix.group + "_Comm1 pit:isCommandOn pit:enableSpeaker .\n" +
				Prefix.group + "_Comm1 pit:hasTimestamp \"%s\"^^xsd:dateTime .\n" +
				Prefix.group + "_Comm1 pit:literalValue \"%b\"^^xsd:boolean .\n" +
				Prefix.group + "_Comm1 pit:hasBeenExecuted \"%b\"^^xsd:boolean .\n");
	}
	
	private int weakEtag;
	
	public AudioPassThroughWebservice(String uriPath, ScheduledExecutorService executor)
	{
		super(uriPath, false, executor, "The enabled state of the audio pass through", 1L);
	}
	
	@Override
	public byte[] getEtag(long contentFormat)
	{
		return Ints.toByteArray(weakEtag & Longs.hashCode(contentFormat));
	}
	
	public byte[] getSerializedResourceStatus(long contentFormat)
	{
		log.debug("Try to create payload (content format: " + contentFormat + ")");
		
		String timestamp = getTimestamp();
		boolean state = getStatus();
		String template = payloadTemplates.get(contentFormat);
		
		if (template == null)
		{
			return null;
		}
		else
		{
			return String.format(template, timestamp, state, true).getBytes(CoapMessage.CHARSET);
		}
	}
	
	@Override
	public boolean isUpdateNotificationConfirmable(InetSocketAddress remoteEndpoint, Token token)
	{
		return false;
	}
	
	@Override
	public void updateEtag(Boolean resourceStatus)
	{
		weakEtag = resourceStatus.hashCode();
	}
}
