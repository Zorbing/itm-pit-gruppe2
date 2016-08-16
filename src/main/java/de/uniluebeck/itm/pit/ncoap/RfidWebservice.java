package de.uniluebeck.itm.pit.ncoap;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import de.uniluebeck.itm.ncoap.communication.dispatching.client.Token;
import de.uniluebeck.itm.ncoap.message.CoapMessage;
import de.uniluebeck.itm.ncoap.message.options.ContentFormat;

public class RfidWebservice extends AbstractObservableWebservice<String>
{
	public static HashMap<Long, String> payloadTemplates = new HashMap<>();
	
	static
	{
		// Add template for plaintext UTF-8 payload
		payloadTemplates.put(
			ContentFormat.TEXT_PLAIN_UTF8,
			"The last read UID (%s) is: %s");
		
		// Add template for XML payload
		payloadTemplates.put(
			ContentFormat.APP_XML,
			"<rfid>\n" +
					"<time>%s</time>\n" +
					"<uid>%s</uid>\n" +
					"</rfid>");
		
		// Add template for Turtle payload
		payloadTemplates.put(
			ContentFormat.APP_TURTLE,
			Prefix.sparql +
				"\n" +
				Prefix.group + "_Pi rdf:type pit:Hardware .\n" +
				Prefix.group + "_Pi pit:hasPart " + Prefix.group + "_RFID .\n" +
				Prefix.group + "_Pi pit:isLocatedIn pit:Room2054 .\n" +
				"\n" +
				Prefix.group + "_RFID rdf:type pit:Hardware .\n" +
				Prefix.group + "_RFID pit:hasSensor " + Prefix.group + "_RFID_Sensor .\n" +
				"\n" +
				Prefix.group + "_RFID_Sensor rdf:type pit:RFIDSensor .\n" +
				Prefix.group + "_RFID_Sensor pit:observesPhenomenon pit:Room2054 .\n" +
				"\n" +
				// static information which will be provided by the SSP
				// "pit:Room2054 rdf:type pit:Phenomenon .\n" +
				// "pit:Room2054 pit:isFeatureOf pit:Building64 .\n" +
				// "\n" +
				Prefix.group + "_Obs1 rdf:type pit:Observation .\n" +
				Prefix.group + "_Obs1 pit:isStatusOf " + Prefix.group + "_RFID_Sensor .\n" +
				Prefix.group + "_Obs1 pit:hasTimestamp \"%s\"^^xsd:dateTime .\n" +
				Prefix.group + "_Obs1 pit:hasValue " + Prefix.group + "_Obs1_Value .\n" +
				"\n" +
				Prefix.group + "_Obs1_Value rdf:type pit:TypeObservationValue .\n" +
				Prefix.group + "_Obs1_Value pit:hasType pit:HexBinary .\n" +
				Prefix.group + "_Obs1_Value pit:literalValue \"%s\"^^xsd:hexBinary .");
	}
	
	private int weakEtag;
	
	public RfidWebservice(String uriPath, ScheduledExecutorService executor)
	{
		super(uriPath, "", executor, "The last read CardId of the RFID", 1L);
	}
	
	public byte[] getEtag(long contentFormat)
	{
		return Ints.toByteArray(weakEtag & Longs.hashCode(contentFormat));
	}
	
	public byte[] getSerializedResourceStatus(long contentFormat)
	{
		log.debug("Try to create payload (content format: " + contentFormat + ")");
		
		String timestamp = getTimestamp();
		String tag = getStatus();
		String template = payloadTemplates.get(contentFormat);
		
		if (template == null)
		{
			return null;
		}
		else
		{
			return String.format(template, timestamp, tag).getBytes(CoapMessage.CHARSET);
		}
	}
	
	@Override
	public boolean isUpdateNotificationConfirmable(InetSocketAddress remoteEndpoint, Token token)
	{
		return false;
	}
	
	@Override
	public void updateEtag(String resourceStatus)
	{
		weakEtag = resourceStatus.hashCode();
	}
}
