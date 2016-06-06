package de.uniluebeck.itm.pit;

import de.uniluebeck.itm.pit.ncoap.LedObservableWebservice;
import de.uniluebeck.itm.pit.ncoap.LoggingConfiguration;
import de.uniluebeck.itm.pit.ncoap.SimpleCoapServer;
import de.uniluebeck.itm.pit.ncoap.SimpleObservableTimeService;

public class App 
{
	public static void main(String[] args) throws Exception
	{
		System.out.println("Program started v2.");
		final int intervalMs = 1000;
		
		// initialize the simple coap server
		LoggingConfiguration.configureDefaultLogging();
		SimpleCoapServer server = new SimpleCoapServer();
		// register led-webservice
		LedObservableWebservice ledService = new LedObservableWebservice("/led", server.getExecutor());
		server.registerService(ledService);
		// register time service
		SimpleObservableTimeService timeService = new SimpleObservableTimeService("/utc-time", 1000, server.getExecutor());
        server.registerService(timeService);
		// register all webservices at the SSP
		server.registerAtSSP();
		
		BrightnessThreshold bthres = new BrightnessThreshold();
		
		while (true)
		{
			boolean status = bthres.run();
			System.out.println(String.format("led status set: %b", status));
			ledService.setResourceStatus(status, intervalMs / 1000);
			Thread.sleep(intervalMs);
		}
//		test.shutdown();
	}
}
