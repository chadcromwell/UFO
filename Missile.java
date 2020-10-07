/********************************************************************************************************************
Author: Chad Cromwell
Date: November 15th, 2017
Assignment: 1
Program: Missile.java
Description: A class that creates a missile and controls its flight patterns
	Missile types:
		Normal - Flies directly towards the player's current position at a slow speed
		Homing - Follows the player as long as the player has a higher elevation that the missile at a medium speed
		Smart - Flies to where the player will be if they are on the move at a fast speed
Methods:
		tick() method - What happens each frame
		render() method - Renders the player sprite as a graphics object
********************************************************************************************************************/

import java.awt.*;
import java.awt.geom.*;
import javax.vecmath.*;

public class Missile extends Rectangle {
	//Finals
	public static final int MISSILESIZE = 32;

	//Missile variables
	double x; //x position
	double y; //y position
	String type; //Type of missile
	double speed; //Speed of missile
	double homingSpeed = 1.5; //Homing missile speed
	double normalSpeed = 1; //Normal missile speed
	double smartSpeed = 5; //Smart missile speed
	double dirSpeed = 0; //Save direction of rocket and speed
	double xSpeed; //Holds speed of x dir
	double ySpeed; //Holds speed of y dir
	double rise; //Holds rise amount
	double run; //Holds run amount
	double m; //Holds slope amount
	float xDist; //Used to hold the distance between the enemy and the player in the x plane, used for orientation of missiles
	float yDist; //Used to hold the distance between the enemy and the player in the y plane, used for orientation of missiles
	float xt; //Time for missile to travel in x direction to reach player
	float yt; //Time for missile to travel in y direction to reach player
	float futureX; //Where the player will be in x direction in future
	float futureY; //Where the player will be in y direction in future
	double angle; //Used to hold the angle in degrees between the enemy and player, used for orientation of missiles
	double missileX; //Holds missile collision top x
	double missileY; //Holds missile collision top y
	double missileX2; //Holds missile collision bottom x
	double missileY2; //Holds missile collision bottom y
	double lMask = 5; //Left collision mask, should be set to 5
	double tMask = 5; //Top collision mask, should be set to 5
	double rMask = 5; //Right collision mask, should be set to 5
	double bMask = 5; //Bottom collision mask, should be set to 5

	//For animation
	private int currentFrame = 0; //Keep track of current frame
	private int swap = 2; //When to swap
	private int displayFrame = 0; //Frame to display
	
	//Missile constructor - Accepts an Enemy object, and string. The string sets the type of missile.
	public Missile(Enemy a, String type) {
		SoundEffect.MISSILE.play();
		//If it's a normal or homing missile
		if(type == "normal" || type == "homing") {
			x = a.x; //Capture x in the Missile object
			y = a.y; //Capture y in the Missile object
			this.type = type; //Capture type in the Missile object
			setBounds((int)x, (int)y, MISSILESIZE, MISSILESIZE); //Set the missile bounds
			xDist = Game.player.x-(float)a.x; //Distance between enemy and player in x plane
			yDist = Game.player.y-(float)a.y; //Distance between enemy and player in y plane
			angle = Math.toDegrees(Math.atan2(yDist, xDist)); //Calculate the angle of the missile
			
			//Calculating slope, used for missile direction (aiming)
			rise = (Game.player.y+(Game.player.PLAYERSIZE/2))-(this.y+(MISSILESIZE/2)); //Rise
			run = (Game.player.x+(Game.player.PLAYERSIZE/2))-(this.x+(MISSILESIZE/2)); //Run
			m = Math.abs(rise/run); //Slope m = rise/run
			xSpeed = normalSpeed; //Horizontal speed
			
			//If the ship is farther in the y direction, it needs to travel fastest in the y direction, not exceeding total speed between both x and y
			if(Math.abs(rise) > Math.abs(run)) {
				ySpeed = -normalSpeed; //Vertical speed
				xSpeed = normalSpeed/m; //xSpeed = the leftover amount of normalSpeed*m 
			}

			//If ship is farther in the x direction, it needs to travel fastest in the x direction, not exceeding total speed between both x and y
			if(Math.abs(rise) < Math.abs(run)) {
				ySpeed = -(normalSpeed*m); //Vertical speed
				xSpeed = normalSpeed; //xSpeed = the leftover amount of normalSpeed*m 
			}

			//If the player is to the left of the missile, the missile will move to the left
			if(Game.player.x+(Game.player.PLAYERSIZE/2) < this.x+(MISSILESIZE/2)) {
				xSpeed = -xSpeed; //Horizontal speed
			}
		}

		//If it's a smart missile
		if(type == "smart") {
			x = a.x; //Capture x in the Missile object
			y = a.y; //Capture y in the Missile object
			this.type = type; //Capture type in the Missile object
			setBounds((int)x, (int)y, MISSILESIZE, MISSILESIZE); //Set the missile bounds
			xDist = Game.player.x-(float)a.x; //Distance between enemy and player in x plane
			yDist = Game.player.y-(float)a.y; //Distance between enemy and player in y plane
			angle = Math.toDegrees(Math.atan2(yDist, xDist)); //Calculate the angle of the missile
			
			//Calculating slope, used for missile direction (aiming)
			rise = (Game.player.y+(Game.player.PLAYERSIZE/2))-(a.y+(MISSILESIZE/2)); //Rise
			run = (Game.player.x+(Game.player.PLAYERSIZE/2))-(a.x+(MISSILESIZE/2)); //Run
			m = Math.abs(rise/run); //Slope m = rise/run
			xSpeed = smartSpeed; //Horizontal speed

			//If the ship is farther in the y direction, it needs to travel fastest in the y direction, not exceeding total speed between both x and y
			if(Math.abs(rise) > Math.abs(run)) {
				ySpeed = -smartSpeed; //Vertical speed
				xSpeed = smartSpeed/m; //xSpeed = the leftover amount of smartSpeed*m 
			}

			//If ship is farther in the x direction, it needs to travel fastest in the x direction, not exceeding total speed between both x and y
			if(Math.abs(rise) < Math.abs(run)) {
				ySpeed = -(smartSpeed*m); //Vertical speed
				xSpeed = smartSpeed; //xSpeed = the leftover amount of smartSpeed*m 
			}

			//Calculate how long it will take the missile to fly in the x direction and y direction
			xt = Math.abs((float)Game.player.x-(float)x)/(float)xSpeed; //How long it will take the missile to reach x position at xSpeed
			yt = Math.abs((float)Game.player.y-(float)y)/(float)ySpeed; //How long it will take the missile to reach y position at ySpeed


			//If player is moving right
			if(Game.player.movingRight) {
				futureX = (float)Game.player.x+((float)Game.player.PLAYERSIZE/2)+((float)xt*(float)Game.player.speed); //Where the player will be in that amount of time in x
				futureY = (float)Game.player.y+((float)Game.player.PLAYERSIZE/2); //The smart enemy does not take into account elevation change
			}
			
			//If player is moving left
			if(Game.player.movingLeft) {
				futureX = (float)Game.player.x+((float)Game.player.PLAYERSIZE/2)-((float)xt*(float)Game.player.speed); //Where the player will be in that amount of time in x
				futureY = (float)Game.player.y+((float)Game.player.PLAYERSIZE/2); //The smart enemy does not take into account elevation change
			}

			//If player is not moving
			if(!Game.player.movingRight && !Game.player.movingLeft) {
				futureX = (float)Game.player.x; //FutureX is actually current x
				futureY = (float)Game.player.y+((float)Game.player.PLAYERSIZE/2); //The smart enemy does not take into account elevation change
			}

			xDist = (float)futureX-(float)a.x; //Distance between enemy and player in x plane
			yDist = (float)futureY-(float)a.y; //Distance between enemy and player in y plane
			angle = Math.toDegrees(Math.atan2(yDist, xDist)); //Calculate the angle of the missile

			//Calculating slope, used for missile direction (aiming)
			rise = (futureY+(Game.player.PLAYERSIZE/2))-(a.y+(MISSILESIZE/2)); //Rise
			run = (futureX+(Game.player.PLAYERSIZE/2))-(a.x+(MISSILESIZE/2)); //Run
			m = Math.abs(rise/run); //Slope m = rise/run

			//If the ship is farther in the y direction, it needs to travel fastest in the y direction, not exceeding total speed between both x and y
			if(Math.abs(rise) > Math.abs(run)) {
				ySpeed = -smartSpeed; //Vertical speed
				xSpeed = smartSpeed/m; //xSpeed = the leftover amount of smartSpeed*m 
			}

			//If ship is farther in the x direction, it needs to travel fastest in the x direction, not exceeding total speed between both x and y
			if(Math.abs(rise) < Math.abs(run)) {
				ySpeed = -(smartSpeed*m); //Vertical speed
				xSpeed = smartSpeed; //xSpeed = the leftover amount of smartSpeed*m 
			}
			
			//If the player is to the left of the missile, the missile will move to the left
			if(Game.player.x+(Game.player.PLAYERSIZE/2) < a.x+(MISSILESIZE/2)) {
				xSpeed = -xSpeed; //Horizontal speed
			}
		}
	}

	//tick() method - What happens each frame
	public void tick() {
		//If the missile is a homing missile
		if(this.type == "homing") {
			speed = homingSpeed; //Set the missile speed to homingSpeed

			//If the missile is below the player
			if(this.y > Game.player.y) {
				this.y -= speed; //Missile flies upwards at speed

				//If missile x is to the left of the player, move right
				if(this.x < Game.player.x) {
					this.x += speed; //Missile flies at speed
					dirSpeed = speed; //Update dirSpeed
				}

				//If missle x is to the right of the player, move left
				if(this.x > Game.player.x) {
					this.x -= speed; //Missile flies at speed
					dirSpeed = -speed; //Update dirSpeed
				}
			}
			else {
				this.y -= speed; //Missile flies upwards at speed
				this.x += dirSpeed; //Apply dirSpeed so it continues with its horizontal movement
			}

			//Handle missile orientation
			xDist = Game.player.x-(float)this.x; //Distance between enemy and player in x plane
			yDist = Game.player.y-(float)this.y; //Distance between enemy and player in y plane
			angle = Math.toDegrees(Math.atan2(yDist, xDist)); //Calculate the angle of the missile
		}

		//If the missile is a normal missile
		if(this.type == "normal") {
			speed = normalSpeed; //Set the missile speed to normalSpeed
			this.x += xSpeed; //Missile flies horizontally at xSpeed
			this.y += ySpeed; //Missile flies vertically at ySpeed

			//If the player is directly above the enemy, there is no run, only rise
			if(run == 0) {
				this.x = this.x; //Missile does not move horizontally
			}
		}

		//If the missile is a smart missile
		if(this.type == "smart") {
			speed = smartSpeed; //Set the missile speed to smartSpeed
			this.x += xSpeed; //Missile flies horizontally at xSpeed
			this.y += ySpeed; //Missile flies vertically at ySpeed

			//If the player is directly above the enemy, there is no run, only rise
			if(run == 0) {
				this.x = this.x; //Missile does not move horizontally
			}
		}

		setBounds((int)x, (int)y, MISSILESIZE, MISSILESIZE); //Update missile bounds
		missileX = this.x+lMask; //Update missileX with masking
		missileY = this.y+tMask; //Update missileY with masking
		missileX2 = this.x+this.width-rMask; //Update missileX2 with masking
		missileY2 = this.y+this.height-bMask; //Update missileY2 with masking

		//Animation handling------------------------------------------------------------------//
		currentFrame++; //Increase currentFrame

		//If it is time for currentFrame to swap
		if(currentFrame == swap) {
			currentFrame = 0; //Set currentFrame back to 0
			displayFrame++; //Go to next displayFrame

			//If displayFrame reaches the max slides it needs to go back to the first displayFrame
			if(displayFrame == Texture.missileSlides) {
				displayFrame = 0; //Set display displayFrame back to index 0
			}
		}

		//Remove missiles outside of window
		for(int i = 0; i < Game.level.missiles.size(); i++) {
			//If the missile is above the top of the window
			if(Game.level.missiles.get(i).y < 0) {
					Game.level.missiles.remove(i); //Remove the missile
			}
		}
	}

	//render() method - Renders the player sprite as a graphics object
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D)g; //Cast graphics object to Graphics2D object
		AffineTransform old = g2d.getTransform(); //Save the transform
		g2d.rotate(Math.toRadians((float)angle), x+width/2, y+height/2); //Rotate so missile will be rendered at proper angle
		g2d.drawImage(Texture.missile[displayFrame], (int)x, (int)y, width, height, null); //Draw the missile
		g2d.setTransform(old); //Revert to old transform
	}
}