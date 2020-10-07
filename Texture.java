/********************************************************************************************************************
Author: Chad Cromwell
Date: November 1st, 2017
Assignment: 1
Program: Texture.java
Description: A class that creates the animation frames for each sprite
********************************************************************************************************************/

import java.awt.image.*;

public class Texture {

	//Texture variables
	public static BufferedImage[] player; //BufferedImage array to hold player frames
	public static BufferedImage[] enemy; //BufferedImage array to hold enemy frames
	public static BufferedImage[] missile; //BufferedImage array to hold missile frames
	public static int playerSlides = 4; //# of drawings for player animation
	public static int enemySlides = 4; //# of drawings for enemy animation
	public static int missileSlides = 4; //# of drawings for missile animation

	//Texture constructor
	public Texture() {
		//Initialize BufferedImage arrays
		player = new BufferedImage[playerSlides]; //Player array
		enemy = new BufferedImage[enemySlides]; //Enemy array
		missile = new BufferedImage[missileSlides]; //Missile array
		
		//Player iteration----------------------------------------------------------------//
		//For each slide, iterate through the image and hold the slides in the player array
		for(int i = 0; i < playerSlides; i++) {
			player[i] = Game.spriteSheet.getSprite(i,0); //Assign the cropped image to player[i]
		}

		//Enemy iteration-----------------------------------------------------------------//
		for(int i = 0; i < enemySlides; i++) {
			enemy[i] = Game.spriteSheet.getSprite(i,1); //Assign the cropped image to enemy[i]
		}

		//Missile iteration---------------------------------------------------------------//
		for(int i = 0; i < missileSlides; i++) {
			missile[i] = Game.spriteSheet.getSprite(i,2); //Assign the cropped image to missile[i]
		}
	}
}