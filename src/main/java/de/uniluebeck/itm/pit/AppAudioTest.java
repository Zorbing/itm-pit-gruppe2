package de.uniluebeck.itm.pit;

import de.uniluebeck.itm.pit.hardware.AudioPassThrough;

public class AppAudioTest
{
	public static void main(String[] args) throws Exception
	{
		System.out.println("Test-Program started.");
		
//		SoundPassThrough.testAudio();
		AudioPassThrough sound = new AudioPassThrough();
		sound.enable();
		sound.start();
	}
}
