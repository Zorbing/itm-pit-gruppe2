package de.uniluebeck.itm.pit;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

import de.uniluebeck.itm.pit.hardware.CardEvent;
import de.uniluebeck.itm.pit.hardware.Rfid;

public class AppRFIDTest implements EventListener
{
	public static void main(String[] args) throws Exception
	{
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		
		Rfid rfid = new Rfid();
		EventListener listener = new AppRFIDTest();
		rfid.addEventListener(Rfid.CARD_DETECTED, listener, false);
		rfid.addEventListener(Rfid.CARD_REMOVED, listener, false);
		
		executor.scheduleAtFixedRate(rfid, 0, 200, TimeUnit.MILLISECONDS);
	}

	@Override
	public void handleEvent(Event event)
	{
		CardEvent evt = (CardEvent) event;
		if (evt.getType().equals(Rfid.CARD_DETECTED))
		{
			System.out.println("Card Read UID: " + ((Rfid)evt.getTarget()).getCurrentUID());
		}
		else if (evt.getType().equals(Rfid.CARD_REMOVED))
		{
			System.out.println("Card removed");
		}
		else
		{
			System.err.println("unknown event type '" + evt.getType() + "'");
		}
	}
}
