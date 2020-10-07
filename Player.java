/********************************************************************************************************************
Author: Chad Cromwell
Date: November 1st, 2017
Assignment: 1
Program: Player.java
Description: A class that creates a controllable player with a UFO sprite
Methods:
		tick() - What happens each frame
		canMove() method - Accepts x and y position, used to determine if the future position will be in collision or not
		render() method - Renders the player sprite as a graphics object
********************************************************************************************************************/

import java.awt.*;
import java.awt.image.*;

public class Player extends Rectangle {
	//Finals
	public static final int PLAYERSIZE = 64; //Size of player

	//For key presses
	public boolean up; //If up is pressed
	public boolean down; //If down is pressed
	public boolean left; //If left is pressed
	public boolean right; //If right is pressed
	public boolean space; //If space is pressed

	//Player variables
	public int speed = 4; //Player speed
	public int hp = 4; //Player hp
	boolean loaded = true; //Whether the player is loaded or not
	int reloadTimer = 0; //Timer to keep track of how long the player has been reloading for
	int reloadTime = 400; //The amount of time it takes for the player to reload, the lower the faster they reload
	int x; //x position
	int y; //y position
	float xSpeed; //Speed in x direction
	float ySpeed; //Speed in y direction
	boolean movingRight = false; //If the player is moving right
	boolean movingLeft = false; //If the player is moving left
	boolean win = false; //If the player has won
	boolean dead = false; //If the player has died
	double playerX; //Player collision top x
	double playerY; //Player collision top y
	double playerX2; //Player collision bottom x
	double playerY2; //Player collision bottom y
	double lMask = 0; //Left collision mask, should be 0
	double tMask = 6; //Top collision mask, should be 6
	double rMask = 0; //Right collision mask, should be 0
	double bMask = 23; //Bottom collision mask, should be 23

	//Crash parameters
	boolean canCrash = true; //Whether the player can crash into a tank or not, keep initialized as true
	boolean crashed = false; //Whether or not the player is currently crashing into an enemy
	double timer; //Keep track of current time in milliseconds, used for crash timer
	double crashTimer = 2000; //How long to allow the player to crash again after leaving enemy collision box, 2000 (2 seconds) is a good setting
	double crashTime; //Keeps track of the time the crash took place

	//For animation
	private int currentFrame = 0; //Keep track of current frame
	private int swap = 40; //When to swap
	private int displayFrame = 0; //Frame to display

	//Player() constructor - Take x and y position
	public Player(int x, int y) {
		this.x = x; //Capture x position in Player object
		this.y = y; //Capture y position in player object
		setBounds(x, y, PLAYERSIZE, PLAYERSIZE); //Set the rectangle bounds
	}

	//tick() - What happens each frame
	public void tick() {
		//Calculate collision box coords
		playerX = Game.player.x+lMask; //Left x
		playerY = Game.player.y+tMask; //Left y
		playerX2 = Game.player.x+Game.player.width-rMask; //Right x
		playerY2 = Game.player.y+Game.player.height-bMask; //Right y

		setBounds(x, y, PLAYERSIZE, PLAYERSIZE); //Update the rectangle bounds
		
		movingRight = false;
		movingLeft = false;

		//Movement------------------------------------------------------------------//
		//If the player is moving up and can move upwards
		if(up && canMove(x, y-speed)) {
			y -= speed; //Minus speed from y (move up)
		}

		//If the player is moving down and can move downwards
		if(down && canMove(x, y+speed)) {
			y += speed; //Add speed to y (move down)
		}

		//If the player is moving left and can move leftwards
		if(left && canMove(x-speed, y)) {
			x -= speed; //Minus speed from x (Move left)
			movingLeft = true;
			movingRight = false;
		}

		//If the player is moving right and can move rightwards
		if(right && canMove(x+speed, y)) {
			x += speed; //Add speed to x (Move right)
			movingRight = true;
			movingLeft = false;
		}

		//Window collision detection------------------------------------------------------------------//
		//If player hits left widow boundary
		if(Game.player.x <= 0) {
			Game.player.x = 0; //Set the player x to 0, keeps them in the play area
		}

		//If player hits right window boundary
		if(Game.player.x+PLAYERSIZE >= Game.WIDTH) {
			Game.player.x = Game.WIDTH-PLAYERSIZE; //Set the player x to Game.WIDTH-PLAYERSIZE, keeps them in the play area
		}

		//If player his top window boundary
		if(Game.player.y <= 0) {
			Game.player.y = 0; //Set the player y to 0, keeps them in the play area
		}

		//If player hits bottom window boundary
		if(Game.player.y+PLAYERSIZE >= Game.HEIGHT) {
			Game.player.y = Game.HEIGHT-PLAYERSIZE; //Set the player y to Game.HEIGHT-PLAYERSIZE, keeps them in the play area
		}

		//Enemy collision detection------------------------------------------------------------------//
		//Iterate through each enemy in the level
		for(int i = 0; i < Game.level.enemies.size(); i++) {
			//If player collides with enemy
			if(Game.level.enemies.get(i).enemyX > playerX && Game.level.enemies.get(i).enemyX < playerX2 || Game.level.enemies.get(i).enemyX2 < playerX2 && Game.level.enemies.get(i).enemyX2 > playerX){
				if(Game.level.enemies.get(i).enemyY > playerY && Game.level.enemies.get(i).enemyY < playerY2 || Game.level.enemies.get(i).enemyY2 < playerY2 && Game.level.enemies.get(i).enemyY2 > playerY){
					//If canCrash is true
					if(canCrash && !crashed){
						Game.level.explosions.add(new Explosion(Game.level.enemies.get(i).x+(Game.level.enemies.get(i).width/2), Game.level.enemies.get(i).y+(Game.level.enemies.get(i).height/2))); //Add an explosion at this location
						Game.player.hp -= 1; //Lower hp by 1
						crashTime = System.currentTimeMillis();
						canCrash = false; //Player cannot crash again yet
						crashed = true; //Player is currently crashing into enemy
					}
				}
			}
			//If the player is currently collided with an enemy (crashed)
			if(crashed) {
				//If the player is not colliding
				if(Game.level.enemies.get(i).enemyX < playerX && Game.level.enemies.get(i).enemyX2 < playerX && Game.level.enemies.get(i).enemyY > playerY2 || Game.level.enemies.get(i).enemyX > playerX2 && Game.level.enemies.get(i).enemyX2 > playerX2 && Game.level.enemies.get(i).enemyY > playerY2 || /******/Game.level.enemies.get(i).enemyX > playerX && Game.level.enemies.get(i).enemyX < playerX2 && Game.level.enemies.get(i).enemyY < playerY2 || Game.level.enemies.get(i).enemyX2 > playerX && Game.level.enemies.get(i).enemyX2 < playerX2 && Game.level.enemies.get(i).enemyY < playerY2) {
					//If the amount of time since the crash is >= to crashTimer
					if(System.currentTimeMillis() - crashTime >= crashTimer) {
						crashed = false; //The player is no longer crashed
						canCrash = true; //The player can now crash again
					}
				}
			}
		}

		//Missile collision detection------------------------------------------------------------------//
		//Iterate through each missile in the level
		for(int i = 0; i < Game.level.missiles.size(); i++) {
			//If player collides with missile
			if(Game.level.missiles.get(i).missileX > playerX && Game.level.missiles.get(i).missileX < playerX2 || Game.level.missiles.get(i).missileX2 < playerX2 && Game.level.missiles.get(i).missileX2 > playerX){
				if(Game.level.missiles.get(i).missileY > playerY && Game.level.missiles.get(i).missileY < playerY2 || Game.level.missiles.get(i).missileY2 < playerY2 && Game.level.missiles.get(i).missileY2 > playerY){
					Game.level.explosions.add(new Explosion(Game.level.missiles.get(i).x+(Game.level.missiles.get(i).width/2), Game.level.missiles.get(i).y+(Game.level.missiles.get(i).height/2))); //Add an explosion at this location
					Game.level.missiles.remove(i); //Remove the missile after collision
					Game.player.hp -= 1; //Lower hp by 1
				}
			}
		}

		//Laser collision detection------------------------------------------------------------------//
		//Iterate through each laser in the level
			for(int i = 0; i < Game.level.lasers.size(); i++) {
				//Iterate through each enemy in the level
				for(int j = 0; j < Game.level.enemies.size(); j++) {
					//If player laser collides with enemy
					if(Game.level.lasers.get(i).lazerX > Game.level.enemies.get(j).enemyX && Game.level.lasers.get(i).lazerX < Game.level.enemies.get(j).enemyX2 || Game.level.lasers.get(i).lazerX2 < Game.level.enemies.get(j).enemyX2 && Game.level.lasers.get(i).lazerX2 > Game.level.enemies.get(j).enemyX){
						if(Game.level.lasers.get(i).lazerY2 > Game.level.enemies.get(j).enemyY){
							Game.level.explosions.add(new Explosion(Game.level.lasers.get(i).x+(Game.level.lasers.get(i).width/2), Game.level.lasers.get(i).y+(Game.level.lasers.get(i).height/2))); //Add an explosion at this location
							Game.level.enemies.remove(j); //Remove the enemy
							Game.level.lasers.remove(i); //Remove the laser
							Game.level.change = true; //An enemy amount change has occured
							break; //Break from loop so it doesn't go OOB
						}
					}
				}
		}

		//Win/Lose conditions------------------------------------------------------------------//
		//If enemies are all destroyed, they win
		if(Game.level.enemies.size() == 0) {
			SoundEffect.WIN.play();
			win = true;
		}

		//If player hp reaches 0, they lose
		if(Game.player.hp == 0) {
			SoundEffect.DEAD.play();
			dead = true;
		}

		//Animation handling------------------------------------------------------------------//
		currentFrame++; //Increase currentFrame

		//If it is time for currentFrame to swap
		if(currentFrame == swap) {
			currentFrame = 0; //Set currentFrame back to 0
			displayFrame++; //Go to next displayFrame

			//If displayFrame reaches the max slides it needs to go back to the first displayFrame
			if(displayFrame == Texture.playerSlides) {
				displayFrame = 0; //Set display displayFrame back to index 0
			}
		}
	}

	//canMove() method - Accepts x and y position, used to determine if the future position will be in collision or not
	private boolean canMove(int nextX, int nextY) {
		Rectangle bounds = new Rectangle(nextX, nextY, width, height); //Create rectangle that represents the future movement position

		//For each tile in the level
		for(int x = 0; x < Game.level.borderTiles.length; x++) {
			for(int y = 0; y < Game.level.borderTiles[0].length; y++) {
				//If the tile isn't null
				if(Game.level.borderTiles[x][y] != null) {
					//Check if the future location will intersect with any of the level borderTiles
					if(bounds.intersects(Game.level.borderTiles[x][y])) {
						return false; //The player cannot move there, so return false
					}
				}
			}
		}
		return true; //The player can move there, so return true
	}

	//render() method - Renders the player sprite as a graphics object
	public void render(Graphics g) {
		g.drawImage(Texture.player[displayFrame], x, y, width, height, null);
	}
}