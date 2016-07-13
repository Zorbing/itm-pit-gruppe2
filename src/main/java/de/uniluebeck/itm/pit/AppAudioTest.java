package de.uniluebeck.itm.pit;

import de.uniluebeck.itm.pit.hardware.AudioPassThrough;

public class AppAudioTest
{
	public static void main(String[] args) throws Exception
	{
		System.out.println("Test program started: audio");
		
//		SoundPassThrough.testAudio();
		AudioPassThrough audio = new AudioPassThrough();
		audio.setEnabled(true);
		audio.start();
	}
}
