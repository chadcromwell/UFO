/********************************************************************************************************************
Author: Chad Cromwell - Based off of "yet another insignificant... programming notes" SoundEffect.java at https://www.ntu.edu.sg/home/ehchua/programming/java/J8c_PlayingSound.html
Date: November 25th, 2017
Assignment: 1
Program: SoundEffect.java
Description: A class that loads sound effects and allows them to be played in the game
Methods:
		play() method - Plays the clip, rewinds if needed
********************************************************************************************************************/

import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;

public enum SoundEffect {
	MISSILE("wav/missile.wav"), EXPLODE("wav/explode.wav"), LAZER("wav/lazer.wav"), WIN("wav/win.wav"), DEAD("wav/dead.wav"); //Sound effects

	public static enum Voume {
		MUTE, LOW, MEDIUM, HIGH; //Volumes, giving the user the ability to change the volume is not implemented in the game itself, however the functionality exists for future purposes
	}
	public static Volume volume = Volume.MEDIUM; //Set to medium
	private Clip clip; //Holds wav clip

	//Constructor, accepts a filename/path
	SoundEffect(String n) {
		try{
			URL url = this.getClass().getClassLoader().getResource(n); //Create url with filename
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url); //Create AudioInputStream with url
			clip = AudioSystem.getClip(); //Assign the wav to clip
			clip.open(audioInputStream); //Open the clip
		}
		catch (UnsupportedAudioFileException e) {
			e.printStackTrace(); //Print stack trace for debugging
		}
		catch (IOException e) {
			e.printStackTrace(); //Print stack trace for debugging
		}
		catch (LineUnavailableException e) {
			e.printStackTrace(); //Print stack trace for debugging
		}
	}

	//play() method - Plays the clip, rewinds if needed
	public void play() {
		//If volume is not muted
		if (volume != Volume.MUTE) {
			//If the clip is running
			if (clip.isRunning()) {
				clip.stop(); //Stop it
			}
			clip.setFramePosition(0); //Rewind
			clip.start(); //Play again
		}
	}
}
