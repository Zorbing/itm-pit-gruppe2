package main;

public class Main
{
	public static void main(String[] args) throws InterruptedException
	{
		System.out.println("Program started.");
		BrightnessThreshold test = new BrightnessThreshold();
		
		while (true)
		{
			test.run();
			Thread.sleep(5000);
		}
//		test.terminate();
	}
}
