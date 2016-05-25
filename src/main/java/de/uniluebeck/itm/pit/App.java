package de.uniluebeck.itm.pit;

/**
 * Hello world!
 *
 */
public class App 
{
	public static void main(String[] args) throws InterruptedException
	{
		System.out.println("Program started.");
		BrightnessThreshold bthres = new BrightnessThreshold();
		
		while (true)
		{
			bthres.run();
			Thread.sleep(100);
		}
//		test.shutdown();
	}
}
