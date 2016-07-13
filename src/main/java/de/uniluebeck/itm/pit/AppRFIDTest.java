package de.uniluebeck.itm.pit;

import java.util.Observable;
import java.util.Observer;

import de.uniluebeck.itm.pit.hardware.Rfid;

public class AppRFIDTest implements Observer
{
	public static void main(String[] args) throws Exception
	{
		Rfid rfid = new Rfid();
		Observer observer = new AppRFIDTest();
		rfid.addObserver(observer);
	}

	@Override
	public void update(Observable rfid, Object arg)
	{
		String uid = ((Rfid) rfid).getCurrentUID();
		if (uid != null)
		{
			System.out.println("Card Read UID: " + uid);
		}
		else
		{
			System.out.println("Card removed");
		}
	}
}
