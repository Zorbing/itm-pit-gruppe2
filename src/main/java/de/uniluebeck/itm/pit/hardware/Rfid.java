package de.uniluebeck.itm.pit.hardware;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventException;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import com.liangyuen.util.Convert;
import com.liangyuen.util.RaspRC522;

public class Rfid extends Thread implements EventTarget
{
	public static final String CARD_DETECTED = "cardDetected";
	public static final String CARD_REMOVED = "cardRemoved";
	
	private String currentUID;
	private Map<String, Set<EventListener>> listenerMap = new HashMap<String, Set<EventListener>>();
	private RaspRC522 rc522;
	private boolean skippedErrorAfterUID;
	
	public Rfid()
	{
		super();
		rc522 = new RaspRC522();
		currentUID = null;
		skippedErrorAfterUID = true;
	}
	
	public String getCurrentUID()
	{
		return currentUID;
	}
	
	public void run()
	{
		int back_len[] = new int[1];
		byte tagid[] = new byte[5];
		
		int rStatus = rc522.Request(RaspRC522.PICC_REQIDL, back_len);
		if (rStatus != RaspRC522.MI_OK && !skippedErrorAfterUID)
		{
			skippedErrorAfterUID = true;
			return;
		}
		if (back_len[0] != 0 && back_len[0] != 16)
		{
			return;
		}
		
		int cStatus = rc522.AntiColl(tagid);
		if (cStatus == RaspRC522.MI_OK)
		{
			String strUID = Convert.bytesToHex(tagid);
			if (back_len[0] == 0 || strUID.equals("0100000000"))
			{
				return;
			}
			
			if (!strUID.equals(currentUID))
			{
				currentUID = strUID;
				cardDetected();
			}
			skippedErrorAfterUID = false;
		}
		else
		{
			if (currentUID != null)
			{
				currentUID = null;
				cardRemoved();
			}
		}
	}

	@Override
	public void addEventListener(String type, EventListener listener, boolean useCapture)
	{
		if (!listenerMap.containsKey(type))
		{
			listenerMap.put(type, new HashSet<EventListener>());
		}
		listenerMap.get(type).add(listener);
	}

	@Override
	public void removeEventListener(String type, EventListener listener, boolean useCapture)
	{
		if (listenerMap.containsKey(type))
		{
			listenerMap.get(type).remove(listener);
		}
	}

	@Override
	public boolean dispatchEvent(Event evt) throws EventException
	{
		if (listenerMap.containsKey(evt.getType()))
		{
			for (EventListener listener : listenerMap.get(evt.getType()))
			{
				listener.handleEvent(evt);
			}
			return true;
		}
		return false;
	}
	
	private boolean cardDetected()
	{
		return dispatchEvent(new CardEvent(CARD_DETECTED, this));
	}
	
	private boolean cardRemoved()
	{
		return dispatchEvent(new CardEvent(CARD_REMOVED, this));
	}
}
