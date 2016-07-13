package de.uniluebeck.itm.pit;

import java.net.URISyntaxException;
import java.util.Observable;
import java.util.Observer;

import de.uniluebeck.itm.pit.ncoap.LedObservableWebservice;
import de.uniluebeck.itm.pit.ncoap.SimpleCoapServer;

public class AppLdrTest implements Observer
{
	private static final int lifeTime = 60 * 60 * 24 * 365;
	
	private LedObservableWebservice ledService;
	private SimpleCoapServer server;
	
	public AppLdrTest() throws URISyntaxException
	{
		// create coap server
		server = new SimpleCoapServer();
		// create web service for led state
		ledService = new LedObservableWebservice("/led", server.getExecutor());
		
		// register led-webservice
		server.registerService(ledService);
		// register all webservices at the SSP
		server.registerAtSSP();
	}
	
	public static void main(String[] args) throws Exception
	{
		System.out.println("Program started v3.");
		
		BrightnessDetector detector = new BrightnessDetector();
		Observer observer = new AppLdrTest();
		detector.addObserver(observer);
		
		while (true)
		{
			Thread.sleep(500);
		}
	}
	
	@Override
	public void update(Observable o, Object arg)
	{
		boolean isDark = ((BrightnessDetector) o).isDark();
		System.out.println(String.format("led state on: %b", isDark));
		ledService.setResourceStatus(isDark, lifeTime);
	}
}
