package de.uniluebeck.itm.pit;

public class App
{
	public static void main(String[] args) throws Exception
	{
		System.out.println("Program started v3.");
		
		BrightnessDetector detector = new BrightnessDetector();
		detector.start();
		
		while (true)
		{
			Thread.sleep(500);
		}
	}
}
