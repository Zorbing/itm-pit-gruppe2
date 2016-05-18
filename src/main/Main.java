package main;

public class Main
{
	public static void main(String[] args) throws InterruptedException
	{
		System.out.println("Program started.");
		BrightnessThreshold test = new BrightnessThreshold();
		test.start();
		
		while (true)
		{
			Thread.sleep(500);
		}
//		test.shutdown();
	}
}
