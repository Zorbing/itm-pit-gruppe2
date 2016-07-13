package de.uniluebeck.itm.pit;

import java.net.URISyntaxException;

import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import de.uniluebeck.itm.pit.hardware.Ldr;
import de.uniluebeck.itm.pit.hardware.Led;
import de.uniluebeck.itm.pit.ncoap.LedObservableWebservice;
import de.uniluebeck.itm.pit.ncoap.LoggingConfiguration;
import de.uniluebeck.itm.pit.ncoap.SimpleCoapServer;

public class AppLdrTest implements GpioPinListenerDigital
{
	private static final int lifeTime = 60 * 60 * 24 * 365;
	
	private Ldr ldr;
	private Led led;
	private LedObservableWebservice ledService;
	private SimpleCoapServer server;
	
	public AppLdrTest() throws Exception
	{
		// initialize the simple coap server
		LoggingConfiguration.configureDefaultLogging();
		
		// create coap server
		server = new SimpleCoapServer();
		// create web service for led state
		ledService = new LedObservableWebservice("/led", server.getExecutor());
		
		ldr = new Ldr(RaspiPin.GPIO_07);
		led = new Led(RaspiPin.GPIO_08);
	}
	
	public static void main(String[] args) throws Exception
	{
		System.out.println("Test program started: LDR");
		
		AppLdrTest ldr = new AppLdrTest();
		ldr.init();
		
		while (true)
		{
			Thread.sleep(500);
		}
	}
	
	public void init() throws URISyntaxException
	{
		// register led-webservice
		server.registerService(ledService);
		// register all webservices at the SSP
		server.registerAtSSP();
		
		// add change listener
		ldr.addChangeListener(this);
		// call the change handler for initialisation
		this.handleGpioPinDigitalStateChangeEvent(null);
	}
	
	@Override
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
	{
		boolean isDark = !ldr.isHigh();
		led.setOn(isDark);
		System.out.println(String.format("led state on: %b", isDark));
		ledService.setResourceStatus(isDark, lifeTime);
	}
}
