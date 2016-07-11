package de.uniluebeck.itm.pit;

import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class AppAudioTest
{
	public static void main(String[] args) throws Exception
	{
		System.out.println("Test-Program started.");
		AudioInputStream audioIn = null;
		boolean useTestSound = true;
		
		AudioFormat format = new AudioFormat(Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false);
		
		DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
		SourceDataLine speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
		speakers.open(format);
		
		DataLine.Info microphoneInfo = new DataLine.Info(TargetDataLine.class, format);
		TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(microphoneInfo);
		if (useTestSound)
		{
			// sample from:
			// https://www.freesound.org/people/Kinoton/sounds/347561/
			URL url = AppAudioTest.class.getClassLoader().getResource("test.wav");
			audioIn = AudioSystem.getAudioInputStream(url);
			audioIn.getFrameLength();
		}
		else
		{
			microphone.open(format);
		}
		
		int numBytesRead;
		int CHUNK_SIZE = 1024;
		System.out.println(microphone.getBufferSize() / 5);
		byte[] data = new byte[microphone.getBufferSize() / 5];
		
		microphone.start();
		speakers.start();
		do
		{
			if (useTestSound)
			{
				numBytesRead = audioIn.read(data, 0, CHUNK_SIZE);
			}
			else
			{
				numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
			}
			// write mic data to stream for immediate playback
			speakers.write(data, 0, numBytesRead);
		}
		while (numBytesRead > 0);
		speakers.close();
		microphone.close();
	}
}
