/********************************************************************************************************************
Author: Chad Cromwell
Date: November 19th, 2017
Assignment: 1
Program: Explosion.java
Description: A class that creates and handles an explosion at sites of collision
Methods:
		tick() method - What happens each frame
		render() method - Renders the explosion as a graphics object
********************************************************************************************************************/

import java.awt.*;

public class Explosion extends Rectangle {
	//Finals
	public static final int EXPLOSIONSIZE = 64; //The width of the explosion, 16 is a good number. The width of the explosion is proportionate to the size of the number.
	public static final int EXPLOSIONSTARTSIZE = 1; //The length the explosion starts at, 5 is a good number. The length of the explosion is proportionate of the size of thithes number.
	
	//Explosion variables
	double x; //Holds explosion top x position
	double y; //Holds explosion top y postiion
	double x2; //Holds explosion bottom x postion
	double y2; //Holds explosion bottom y position
	double speed = 6; //Speed in which the explosion spreads
	int[] explosionColour = {255, 128, 0}; //Set the colour of the explosion in RGB Ex. {255, 128, 0} = organge, {0, 255, 0} = green, {255,255,255} = white
	
	//Explosion constructor - Accepts a player object
	public Explosion(double x, double y) {
		SoundEffect.EXPLODE.play();
		this.x = x-(EXPLOSIONSTARTSIZE/2); //Capture Player x in the Explosion object
		this.y = y-(EXPLOSIONSTARTSIZE/2); //Capture Player y in the Explosion object
		this.x2 = x+EXPLOSIONSTARTSIZE; //Assign x2 size
		this.y2 = y+EXPLOSIONSTARTSIZE; //Assign y2 size
		setBounds((int)x, (int)y, (int)x2, (int)y2); //Set the explosion bounds
	}

	//tick() method - What happens each frame
	public void tick() {
		//If the explosion isn't to full size yet
		if(Math.abs(x-x2) < EXPLOSIONSIZE) {
			this.x -= speed; //x to the left
			this.y -= speed; //y to the top
			this.x2 += speed; //x2 to the right
			this.y2 += speed; //y2 to the bottom
		}

		setBounds((int)x, (int)y, (int)x2, (int)y2); //Update the explosion bounds

		//Iterate through the explosions
		for(int i = 0; i < Game.level.explosions.size(); i++) {
			//If the explosion reaches the proper size
			if(Math.abs(x-x2) >= EXPLOSIONSIZE) {
				Game.level.explosions.remove(i); //Remove the explosion
			}
		}
	}

	//render() method - Renders the explosion as a graphics object
	public void render(Graphics g) {
		//Draws the explosion as a rectangle
		Graphics2D g2d = (Graphics2D) g; //Convert graphics object to Graphics2D object
		g2d.setColor(new Color(explosionColour[0], explosionColour[1], explosionColour[2])); //Set the colour of the explosion
		g2d.fillRect((int)x, (int)y, (int)x2-(int)x, (int)y2-(int)y); //Draw the explosion
	}
}