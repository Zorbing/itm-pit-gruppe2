package de.uniluebeck.itm.pit.hardware;

import java.util.Observable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.liangyuen.util.Convert;
import com.liangyuen.util.RaspRC522;

public class Rfid extends Observable
{
	private String currentUID = null;
	private RfidWrapper rfidWrapper;
	
	private class RfidWrapper extends Thread
	{
		private RaspRC522 rc522;
		private boolean skippedErrorAfterUID;
		
		public RfidWrapper()
		{
			super();
			
			rc522 = new RaspRC522();
			currentUID = null;
			skippedErrorAfterUID = true;
		}
		
		@Override
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
					setCurrentUID(strUID);
				}
				skippedErrorAfterUID = false;
			}
			else
			{
				if (currentUID != null)
				{
					setCurrentUID(null);
				}
			}
		}
	}
	
	public Rfid()
	{
		super();
		
		rfidWrapper = new RfidWrapper();
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(rfidWrapper, 0, 200, TimeUnit.MILLISECONDS);
	}
	
	public String getCurrentUID()
	{
		return currentUID;
	}
	
	private void setCurrentUID(String newUID)
	{
		currentUID = newUID;
		setChanged();
		notifyObservers();
	}
}
