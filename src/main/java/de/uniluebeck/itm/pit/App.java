package de.uniluebeck.itm.pit;

import de.uniluebeck.itm.pit.ncoap.LedObservableWebservice;
import de.uniluebeck.itm.pit.ncoap.LoggingConfiguration;
import de.uniluebeck.itm.pit.ncoap.SimpleCoapServer;

/**
 * Hello world!
 *
 */
public class App 
{
	public static void main(String[] args) throws Exception
	{
		System.out.println("Program started.");
		final int intervalMs = 1000;
		
		// initialize the simple coap server
		LoggingConfiguration.configureDefaultLogging();
		SimpleCoapServer server = new SimpleCoapServer();
		// register led-webservice
		LedObservableWebservice ledService = new LedObservableWebservice("/led", server.getExecutor());
		server.registerService(ledService);
		// register all webservices at the SSP
		server.registerAtSSP();
		
		BrightnessThreshold bthres = new BrightnessThreshold();
		
		while (true)
		{
			boolean status = bthres.run();
			ledService.setResourceStatus(status, intervalMs / 1000);
			Thread.sleep(100);
		}
//		test.shutdown();
	}
}
