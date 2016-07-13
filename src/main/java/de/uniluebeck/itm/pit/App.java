package de.uniluebeck.itm.pit;

import java.util.Observable;
import java.util.Observer;

import de.uniluebeck.itm.pit.hardware.AudioPassThrough;
import de.uniluebeck.itm.pit.hardware.Rfid;

public class App implements Observer
{
	private AudioPassThrough audio;
	private String enteredUid = null;
	
	public App() throws Exception
	{
		audio = new AudioPassThrough();
		audio.start();
	}
	
	public static void main(String[] args) throws Exception
	{
		System.out.println("Program started.");
		App app = new App();
		
		Rfid rfid = new Rfid();
		rfid.addObserver(app);
	}
	
	@Override
	public void update(Observable rfid, Object arg)
	{
		String uid = ((Rfid) rfid).getCurrentUID();
		if (uid != null)
		{
			System.out.println("Card Read UID: " + uid);
			if (enteredUid == null)
			{
				enteredUid = uid;
			}
			else if (enteredUid.equals(uid))
			{
				enteredUid = null;
			}
			audio.setEnabled(enteredUid != null);
		}
		else
		{
			System.out.println("Card removed");
		}
	}
}
