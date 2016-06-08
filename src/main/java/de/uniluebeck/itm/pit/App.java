package de.uniluebeck.itm.pit;

import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import de.uniluebeck.itm.pit.ncoap.LedObservableWebservice;
import de.uniluebeck.itm.pit.ncoap.LoggingConfiguration;
import de.uniluebeck.itm.pit.ncoap.SimpleCoapServer;

public class App implements GpioPinListenerDigital
{
	private static final int lifeTime = 60 * 60 * 24 * 365;
	private BrightnessThreshold brightnessThreshold;
	private LedObservableWebservice ledService;
	private SimpleCoapServer server;
	
	public App() throws Exception
	{
		// initialize the simple coap server
		LoggingConfiguration.configureDefaultLogging();
		
		server = new SimpleCoapServer();
		// register led-webservice
		ledService = new LedObservableWebservice("/led", server.getExecutor());
		server.registerService(ledService);
		// register all webservices at the SSP
		server.registerAtSSP();
		
		brightnessThreshold = new BrightnessThreshold(this);
		this.handleGpioPinDigitalStateChangeEvent(null);
	}
	
	@Override
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
	{
		boolean status = brightnessThreshold.run();
		System.out.println(String.format("led status set: %b", status));
		ledService.setResourceStatus(!status, lifeTime);
	}
	
	public static void main(String[] args) throws Exception
	{
		System.out.println("Program started v3.");
		
		new App();
		
		while (true)
		{
			Thread.sleep(500);
		}
	}
}
