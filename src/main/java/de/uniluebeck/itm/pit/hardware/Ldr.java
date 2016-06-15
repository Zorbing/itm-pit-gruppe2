package de.uniluebeck.itm.pit.hardware;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class Ldr
{
	private GpioController gpio;
	private GpioPinDigitalInput ldrPin;
	
	public Ldr(Pin pin)
	{
		// create a gpio controller
		gpio = GpioFactory.getInstance();
		// connect to pin
		ldrPin = gpio.provisionDigitalInputPin(pin, "myLDR");
	}
	
	public void addChangeListener(GpioPinListenerDigital changeListener)
	{
		ldrPin.addListener(changeListener);
	}
	
	public boolean isHigh()
	{
		return ldrPin.getState() == PinState.HIGH;
	}
	
	public void removeChangeListener(GpioPinListenerDigital changeListener)
	{
		ldrPin.removeListener(changeListener);
	}
}
