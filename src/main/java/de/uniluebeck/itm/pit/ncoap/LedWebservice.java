package de.uniluebeck.itm.pit.ncoap;

import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.ScheduledExecutorService;

import de.uniluebeck.itm.ncoap.communication.dispatching.client.Token;
import de.uniluebeck.itm.ncoap.message.CoapMessage;
import de.uniluebeck.itm.ncoap.message.options.ContentFormat;

public class LedWebservice extends AbstractObservableWebservice<Boolean>
{
	public static HashMap<Long, String> payloadTemplates = new HashMap<>();
	
	static
	{
		// Add template for plaintext UTF-8 payload
		payloadTemplates.put(
			ContentFormat.TEXT_PLAIN_UTF8,
			"The LED is currently on: %B");
		
		// Add template for XML payload
		payloadTemplates.put(
			ContentFormat.APP_XML,
			"<ledState>\n" +
				"<time>%s</time>\n" +
				"<state>%b</state>\n" +
				"</ledState>");
		
		// Add template for Turtle payload
		payloadTemplates.put(
			ContentFormat.APP_TURTLE,
			Prefix.sparql+
				"\n" +
				Prefix.group + "_Pi rdf:type pit:Hardware .\n" +
				Prefix.group + "_Pi pit:hasPart " + Prefix.group + "_LDR .\n" +
				Prefix.group + "_Pi pit:isLocatedIn pit:Room2054 .\n" +
				"\n" +
				Prefix.group + "_LDR rdf:type pit:GL5516-LDR .\n" +
				Prefix.group + "_LDR pit:hasSensor " + Prefix.group + "_LDR_Sensor .\n" +
				"\n" +
				Prefix.group + "_LDR_Sensor rdf:type pit:LightSensorBinary .\n" +
				Prefix.group + "_LDR_Sensor pit:observesPhenomenon pit:Room2054 .\n" +
				"\n" +
				// static information which will be provided by the SSP
				// "pit:Room2054 rdf:type pit:Phenomenon .\n" +
				// "pit:Room2054 pit:isFeatureOf pit:Building64 .\n" +
				// "\n" +
				Prefix.group + "_Obs1 rdf:type pit:Observation .\n" +
				Prefix.group + "_Obs1 pit:isStatusOf " + Prefix.group + "_LDR_Sensor .\n" +
				Prefix.group + "_Obs1 pit:hasTimestamp \"%s\"^^xsd:dateTime .\n" +
				Prefix.group + "_Obs1 pit:hasValue " + Prefix.group + "_Obs1_Value .\n" +
				"\n" +
				Prefix.group + "_Obs1_Value rdf:type pit:TypeObservationValue .\n" +
				Prefix.group + "_Obs1_Value pit:hasType pit:Boolean .\n" +
				Prefix.group + "_Obs1_Value pit:literalValue \"%b\"^^xsd:boolean .");
	}
	
	public LedWebservice(String uriPath, ScheduledExecutorService executor)
	{
		super(uriPath, false, executor, "The state of the LED", 1L);
	}
	
	public byte[] getEtag(long contentFormat)
	{
		return new byte[] { (byte) (getStatus() ? 1 : 0) };
	}
	
	public byte[] getSerializedResourceStatus(long contentFormat)
	{
		log.debug("Try to create payload (content format: " + contentFormat + ")");
		
		DateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String timestamp = simpleDateFormat.format(new Date());
		boolean state = getStatus();
		String template = payloadTemplates.get(contentFormat);
		
		if (template == null)
		{
			return null;
		}
		else
		{
			return String.format(template, timestamp, state).getBytes(CoapMessage.CHARSET);
		}
	}
	
	@Override
	public boolean isUpdateNotificationConfirmable(InetSocketAddress remoteEndpoint, Token token)
	{
		return false;
	}
}
