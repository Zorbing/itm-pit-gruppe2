package de.uniluebeck.itm.pit;

import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class AppAudioTest
{
	public static void main(String[] args) throws Exception
	{
		System.out.println("Test-Program started.");
		
		// testAudio();
		testMicrophone();
	}
	
	public static void testAudio() throws Exception
	{
		// sample from: https://www.freesound.org/people/Kinoton/sounds/347561/
		URL url = AppAudioTest.class.getClassLoader().getResource("test.wav");
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
	
	public static void testMicrophone() throws Exception
	{
		AudioFormat format = new AudioFormat(Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false);
		
		DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
		SourceDataLine speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
		speakers.open(format);
		
		DataLine.Info microphoneInfo = new DataLine.Info(TargetDataLine.class, format);
		TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(microphoneInfo);
		microphone.open(format);
		
		int numBytesRead;
		int CHUNK_SIZE = 1024;
		System.out.println(microphone.getBufferSize() / 5);
		byte[] data = new byte[microphone.getBufferSize() / 5];
		
		microphone.start();
		speakers.start();
		do
		{
			numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
			// write mic data to stream for immediate playback
			speakers.write(data, 0, numBytesRead);
		}
		while (numBytesRead > 0);
		speakers.close();
		microphone.close();
	}
}
