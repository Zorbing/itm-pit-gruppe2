package main;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListener;


public class BrightnessThreshold implements GpioPinListener
{
	private GpioController gpio;
	private GpioPinDigitalInput ldrPin;
	private GpioPinDigitalOutput ledPin;
	
	public BrightnessThreshold()
	{
		// create a gpio controller
		gpio = GpioFactory.getInstance();
		
		ldrPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07, "myLDR");
		
		ledPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08, "myLED", PinState.HIGH);
		ledPin.setShutdownOptions(true, PinState.LOW);
	}
	
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
	{
        setLED(isLow());
    }
	
    private boolean isLow()
	{
		return ldrPin.getState() == PinState.LOW;
	}
	
	private void setLED(boolean state)
	{
		ledPin.setState(state);
	}
	
	public void shutdown()
	{
		// stop all GPIO activity/threads by shutting down the GPIO controller
		// (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
		gpio.shutdown();
	}
	
	public void start()
	{
		ldrPin.addListener(this);
	}
}
