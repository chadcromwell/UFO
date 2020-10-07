/********************************************************************************************************************
Author: Chad Cromwell
Date: November 16th, 2017
Assignment: 1
Program: Laser.java
Description: A class that creates and controls a laser fired from the player's UFO
Methods:
		tick() method - What happens each frame
		render() method - Renders the laser as a graphics object
********************************************************************************************************************/

import java.awt.*;

public class Laser extends Rectangle {
	//Finals
	public static final int LASERSIZE = 16; //The width of the laser, 16 is a good number. The width of the laser is proportionate to the size of the number.
	public static final int LASERSTARTSIZE = 5; //The length the laser starts at, 5 is a good number. The length of the laser is proportionate of the size of thithes number.
	
	//Laser variables
	double x; //Holds laser top x position
	double y; //Holds laser top y postiion
	double x2; //Holds laser bottom x postion
	double y2; //Holds laser bottom y position
	double lazerX; //Holds laser collision top x position
	double lazerX2; //Holds laser collision bottom x position
	double lazerY; //Holds laser collision top y position
	double lazerY2; //Holds laser collision bottom y position
	double speed = 10; //Speed of laser
	double lazerLength = 50; //Length of the laser
	int[] lazerColour = {0, 255, 0}; //Set the colour of the laser in RGB Ex. {0, 255, 0} = green, {255,255,255} = white
	
	//Laser constructor - Accepts a player object
	public Laser(Player a) {
		SoundEffect.LAZER.play();
		this.x = a.x; //Capture Player x in the Laser object
		this.y = a.y; //Capture Player y in the Laser object
		this.x2 = LASERSIZE; //Assign x2 size
		this.y2 = LASERSTARTSIZE; //Assign y2 size
		lazerX = (a.x+(a.width/2)-(LASERSIZE/2)); //Capture x in the Laser obect
		lazerX2 = (a.x+(a.width/2)+(LASERSIZE/2)); //Capture x in the Laser obect
		lazerY = (a.y+(a.height/2)); //Capture y in the Laser object
		lazerY2 = a.y; //Capture y in the Laser object
		setBounds((int)x, (int)y, (int)x2, (int)lazerY2); //Set the laser bounds
	}

	//tick() method - What happens each frame
	public void tick() {
		//Grow the laser out the bottom of the UFO
		//If the y2 amount isn't at lazerLength yet
		if(y2 < lazerLength) {
			y2 += speed; //Stretch the bottom y index at the laser speed
			lazerY2 = lazerY+y2; //Update lazerY2 position (world coords)

		}

		//If the laser is at the proper length
		if(y2 >= lazerLength) {
			lazerY += speed; //Move the top y index at the laser speed
			y = lazerY; //Update y position
			y2 = lazerLength; //Move the bottom y index at the laser speed
			lazerY2 = lazerY+y2; //Update lazerY2 position (world coords)
		}

		setBounds((int)lazerX, (int)lazerY, (int)x2, (int)y2); //Update the laser bounds

		//Iterate through each laser
		for(int i = 0; i < Game.level.lasers.size(); i++) {
			//If the laser hits the ground
			if((this.lazerY+lazerLength) > Game.level.height-37) {
					lazerLength -= speed; //Shrink the laser
					//If the laser is too short (if now too small to see)
					if(lazerLength <= 0) {
						Game.level.lasers.remove(i); //Remove the laser
					}
					break; //Break from the current loop
			}
		}
	}

	//render() method - Renders the laser as a graphics object
	public void render(Graphics g) {
		//Draws the laser as a rectangle
		Graphics2D g2d = (Graphics2D) g; //Convert graphics object to Graphics2D object
		g2d.setColor(new Color(lazerColour[0], lazerColour[1], lazerColour[2])); //Set the colour of the laser
		g2d.fillRect((int)lazerX, (int)lazerY, (int)x2, (int)y2); //Draw the laser
	}
}