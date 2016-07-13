package de.uniluebeck.itm.pit.hardware;

import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import de.uniluebeck.itm.pit.AppAudioTest;

public class AudioPassThrough extends Thread
{
	private static final int CHUNK_SIZE = 1024;
	
	private boolean enabled = false;
	private TargetDataLine microphone;
	private SourceDataLine speakers;
	
	public AudioPassThrough() throws LineUnavailableException
	{
		AudioFormat format = new AudioFormat(Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false);
		
		DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
		DataLine.Info microphoneInfo = new DataLine.Info(TargetDataLine.class, format);
		
		speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
		speakers.open(format);
		
		microphone = (TargetDataLine) AudioSystem.getLine(microphoneInfo);
		microphone.open(format);
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
	
	public void disable()
	{
		enabled = false;
	}
	
	public void enable()
	{
		enabled = true;
	}
	
	@Override
	public void run()
	{
		int numBytesRead;
		System.out.println(microphone.getBufferSize() / 5);
		byte[] data = new byte[microphone.getBufferSize() / 5];
		
		microphone.start();
		speakers.start();
		do
		{
			numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
			// write mic data to stream for immediate playback
			if (enabled)
			{
				speakers.write(data, 0, numBytesRead);
			}
		}
		while (numBytesRead > 0);
		speakers.close();
		microphone.close();
	}
}
