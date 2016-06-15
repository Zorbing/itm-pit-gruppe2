package de.uniluebeck.itm.pit;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AppTest
{
	public static void main(String[] args) throws Exception
	{
		System.out.println("Test-Program started.");
		
		// sample from: https://www.freesound.org/people/Kinoton/sounds/347561/
		URL url = AppTest.class.getClassLoader().getResource("test.wav");
		AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
		// Get a sound clip resource.
		Clip clip = AudioSystem.getClip();
		// Open audio clip and load samples from the audio input stream.
		clip.open(audioIn);
		
		clip.start();
		do
		{
			Thread.sleep(500);
		}
		while (clip.isRunning());
	}
}
