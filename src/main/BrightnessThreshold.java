package main;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
* This example code demonstrates how to perform simple state
* control of a GPIO pin on the Raspberry Pi.  
* 
* @author Robert Savage
*/
public class BrightnessThreshold
{
	private GpioController gpio;
	private GpioPinDigitalOutput ledPin;
	private GpioPinDigitalInput ldrPin;
	
	public BrightnessThreshold()
	{
		// create gpio controller
		gpio = GpioFactory.getInstance();
		
		ledPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08, "myLED", PinState.HIGH);
		ledPin.setShutdownOptions(true, PinState.LOW);
		
		ldrPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07, "myLDR");
	}
	
	public void terminate()
	{
		// stop all GPIO activity/threads by shutting down the GPIO controller
		// (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
		gpio.shutdown();
	}
	
	private void setLED(boolean state)
	{
		ledPin.setState(state);
	}
	
	private boolean isHigh()
	{
		return ldrPin.getState() == PinState.HIGH;
	}
	
	public void run()
	{
		setLED(isHigh());
	}
}
