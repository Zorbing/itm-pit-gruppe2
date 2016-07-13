package de.uniluebeck.itm.pit.hardware;

import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import de.uniluebeck.itm.pit.AppAudioTest;

public class AudioPassThrough extends Thread
{
	private static final int CHUNK_SIZE = 64;
	
	private boolean enabled = false;
	private TargetDataLine microphone;
	private SourceDataLine speaker;
	
	public AudioPassThrough() throws LineUnavailableException
	{
		AudioFormat format = new AudioFormat(Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false);
		
		DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
		DataLine.Info microphoneInfo = new DataLine.Info(TargetDataLine.class, format);
		
		speaker = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
		speaker.open(format);
		
		microphone = (TargetDataLine) AudioSystem.getLine(microphoneInfo);
		microphone.open(format);
		
		printDebugInformation();
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
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	@Override
	public void run()
	{
		int numBytesRead;
		System.out.println("buffer size: " + microphone.getBufferSize() / 5);
		byte[] data = new byte[microphone.getBufferSize() / 5];
		
		microphone.start();
		speaker.start();
		do
		{
			numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
			// write mic data to stream for immediate playback
			if (enabled)
			{
				speaker.write(data, 0, numBytesRead);
			}
		}
		while (numBytesRead > 0);
		speaker.close();
		microphone.close();
	}
	
	private void printDebugInformation()
	{
		System.out.format("\nSpeaker Info! class: %s\n", speaker.getClass().getName());
		AudioFormat sFormat = speaker.getFormat();
		Line.Info sInfo = speaker.getLineInfo();
		System.out.format("encoding: %s; channels: %d; sample rate: %f; sample size: %d; frame rate: %f; frame size: %d\n", sFormat.getEncoding(), sFormat.getChannels(), sFormat.getSampleRate(), sFormat.getSampleSizeInBits(), sFormat.getFrameRate(), sFormat.getFrameSize());
		System.out.format("level: %f\n", speaker.getLevel());
		System.out.format("class name: %s; to string: %s\n", sInfo.getLineClass().getName(), sInfo.toString());
		
		System.out.format("\nMicrophone Info! class: %s\n", microphone.getClass().getName());
		AudioFormat mFormat = microphone.getFormat();
		Line.Info mInfo = microphone.getLineInfo();
		System.out.format("encoding: %s; channels: %d; sample rate: %f; sample size: %d; frame rate: %f; frame size: %d\n", mFormat.getEncoding(), mFormat.getChannels(), mFormat.getSampleRate(), mFormat.getSampleSizeInBits(), mFormat.getFrameRate(), mFormat.getFrameSize());
		System.out.format("level: %f\n", microphone.getLevel());
		System.out.format("class name: %s; to string: %s\n\n", mInfo.getLineClass().getName(), mInfo.toString());
	}
}
