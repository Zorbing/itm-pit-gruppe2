package de.uniluebeck.itm.pit;

import java.util.Observable;
import java.util.Observer;

import de.uniluebeck.itm.pit.hardware.Rfid;
import de.uniluebeck.itm.pit.hardware.AudioPassThrough;

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
				audio.enable();
			}
			else if (enteredUid == uid)
			{
				enteredUid = null;
				audio.disable();
			}
		}
		else
		{
			System.out.println("Card removed");
		}
	}
}
