package de.uniluebeck.itm.pit;

import de.uniluebeck.itm.pit.hardware.SoundPassThrough;

public class AppAudioTest
{
	public static void main(String[] args) throws Exception
	{
		System.out.println("Test-Program started.");
		
//		SoundPassThrough.testAudio();
		SoundPassThrough sound = new SoundPassThrough();
		sound.enable();
		sound.start();
	}
}
