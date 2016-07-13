package de.uniluebeck.itm.pit;

import java.util.Observable;

import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import de.uniluebeck.itm.pit.hardware.Ldr;
import de.uniluebeck.itm.pit.hardware.Led;
import de.uniluebeck.itm.pit.ncoap.LoggingConfiguration;

public class BrightnessDetector extends Observable implements GpioPinListenerDigital
{
	private boolean lastState;
	private Ldr ldr;
	private Led led;
	
	public BrightnessDetector() throws Exception
	{
		// initialize the simple coap server
		LoggingConfiguration.configureDefaultLogging();
		
		ldr = new Ldr(RaspiPin.GPIO_07);
		led = new Led(RaspiPin.GPIO_08);
		
		// add change listener
		ldr.addChangeListener(this);
		// call the change handler one first time
		this.handleGpioPinDigitalStateChangeEvent(null);
	}
	
	public boolean isDark()
	{
		return lastState;
	}
	
	@Override
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
	{
		boolean state = !ldr.isHigh();
		led.setOn(state);
		if (state != lastState)
		{
			lastState = state;
			setChanged();
			notifyObservers();
		}
	}
}
