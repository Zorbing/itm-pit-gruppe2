package de.uniluebeck.itm.pit.hardware;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

public class Led
{
	private GpioController gpio;
	private GpioPinDigitalOutput ledPin;
	
	public Led(Pin pin)
	{
		// create a gpio controller
		gpio = GpioFactory.getInstance();
		
		ledPin = gpio.provisionDigitalOutputPin(pin, "myLED", PinState.HIGH);
		ledPin.setShutdownOptions(true, PinState.LOW);
	}
	
	public void setOn(boolean state)
	{
		ledPin.setState(state);
	}
}
