package de.uniluebeck.itm.pit;

import java.net.URISyntaxException;
import java.util.Observable;
import java.util.Observer;

import de.uniluebeck.itm.pit.hardware.AudioPassThrough;
import de.uniluebeck.itm.pit.hardware.Rfid;
import de.uniluebeck.itm.pit.ncoap.LoggingConfiguration;
import de.uniluebeck.itm.pit.ncoap.RfidWebservice;
import de.uniluebeck.itm.pit.ncoap.SimpleCoapServer;

public class App implements Observer
{
	private static final int lifetime = 60 * 10;
	
	private AudioPassThrough audio;
	private String enteredUid = null;
	private RfidWebservice rfidService;
	private SimpleCoapServer server;
	
	public App() throws Exception
	{
		// initialize the simple coap server
		LoggingConfiguration.configureDefaultLogging();
		
		// create coap server
		server = new SimpleCoapServer();
		// create web service for rfid-uid
		rfidService = new RfidWebservice("/rfid", server.getExecutor());
		
		audio = new AudioPassThrough();
		audio.start();
	}
	
	public static void main(String[] args) throws Exception
	{
		System.out.println("Program started.");
		App app = new App();
		
		Rfid rfid = new Rfid();
		rfid.addObserver(app);
		
		app.init();
	}
	
	public void init() throws URISyntaxException
	{
		// register led-webservice
		server.registerService(rfidService);
		// register all webservices at the SSP
		server.registerAtSSP();
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
			
			// update last read card id in SSP
			rfidService.setResourceStatus(uid, lifetime);
		}
		else
		{
			System.out.println("Card removed");
		}
	}
}
