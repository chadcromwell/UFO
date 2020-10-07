/********************************************************************************************************************
Author: Chad Cromwell
Date: November 1st, 2017
Assignment: 1
Program: Enemy.java
Description: A class that creates an enemy which can have one of the following types of intelligence:
		stupid - Has bad aim , moves slowly, fires a missile only when the player passes above them within proximity
		normal - Has good aim, strafes from left to right, fires often, fires a missile to where the player currently is
		coordinated - Fires in coordination with one other coordinated tank, fires homing missiles, has a slow movement speed, and positions themselves on either side of the player. Only reloads when in position.
		evasive - Always flees from the player when the player gets too close, moves quickly, fires missiles at the player's current position
		smart - Follows the player, shoots a fast missile, and leads it's missiles to shoot where the player will be if they are on the move
Methods:
		tick() method - What happens each frame
		coordinatedAttack method - Accepts two enemies, and coordinates an attack between the two of them
		render() method - Renders the enemy sprite as a graphics object
********************************************************************************************************************/

import java.awt.*;
import java.lang.*;

public class Enemy extends Rectangle {
	
	//Finals
	public static final int ENEMYSIZE = 64; //Size of the enemy
	public static final int PROXIMITY = 100; //Proximity used for evasive enemy, how close to the enemy before it starts fleeing
	
	//Enemy variables
	String type; //Type of enemy, refer to Type of AI above for acceptable Strings
	double x; //x position
	double y; //y position
	int dir; //Used to determine the direction the enemy is facing so that sprite can be flipped
	int reloadTime = 0; //How long the enemy takes to reload
	int reloadTimer = 0; //Counts long the enemy has been reloading
	int stupidReloadTime = 50; //How long it takes for stupid enemies to reload
	int normalReloadTime = 150; //How long it takes for normal enemies to reload
	int coordinatedReloadTime = 500; //How long it takes for normal enemies to reload
	int evasiveReloadTime = 200; //How long it takes for evasive enemies to reload
	int smartReloadTime = 500; //How long it takes for smart enemies to reload

	double speed; //Speed of enemy
	double stupidSpeed = .5; //Speed of stupid enemy
	double normalSpeed = 1.5; //Speed of normal enemy
	double coordinatedSpeed = 1; //Speed of normal enemy
	double evasiveSpeed = 3; //Speed of evasive enemy
	double smartSpeed = 2; //Speed of smart enemy

	double enemyX; //Holds enemy collision top x
	double enemyY; //Holds enemy collision top y
	double enemyX2; //Holds enemy collision bottom x
	double enemyY2; //Holds enemy collision bottom y
	double lMask = 0; //Left collision mask, should be set to 0
	double tMask = 28; //Top collision mask, should be set to 28
	double rMask = 0; //Right collision mask, should be set to 0
	double bMask = 0; //Bottom collision mask, should be set to 0

	boolean atEdge; //If enemy is at the edge
	boolean toLeft; //If enemy is to the left of the player
	boolean toRight; //If enemy is to the right of the player
	boolean proximity; //If enemy is within proximity of the player
	boolean fleeRight; //If enemy is fleeing right
	boolean fleeLeft; //If enemy is fleeing left
	boolean loaded; //If the enemy is loaded
	boolean canFire; //If the enemy can fire
	boolean readyToFire; //If the enemy is ready to fire
	boolean left; //If the enemy is facing left
	boolean right; //If the enemy is facing right
	boolean coordAttackingMovement = true; //Used to determine if enemies can move in coordinated attack
	
	//Enemy(double x, double y, String type) constructor - Takes an x, y position and type of enemy and creates one
	public Enemy(double x, double y, String type) {
		this.type = type; //Capture type within object
		this.x = x; //Capture x within object
		this.y = y; //Capture y within object
		setBounds((int)x, (int)y, ENEMYSIZE, ENEMYSIZE); //Set the bounds of the enemy object
	}

	//tick() method - What happens each frame
	public void tick() {
		enemyX = this.x+lMask; //Update enemyX
		enemyY = this.y+tMask; //Update enemyY
		enemyX2 = this.x+this.width-rMask; //Update enemyX2
		enemyY2 = this.y+this.height-bMask; //Update enemyY2

		//Update bounds for enemy for collision detection
		setBounds((int)this.x, (int)this.y, ENEMYSIZE, ENEMYSIZE);

		//Keep enemy within window
		//If enemy goes outside left window
		if(this.x < 0) {
			this.x = 0; //Set x to 0
		}

		//If enemy goes outside right window
		if(this.x+Enemy.ENEMYSIZE > Game.WIDTH) {
			this.x = Game.WIDTH-Enemy.ENEMYSIZE; //Set x to right edge-enemy width
		}
		
		//Positioning Detection---------------------------------------------------------------------------------//
		//Find centre position of enemy and player
		double xPos = (this.x+(this.ENEMYSIZE/2)); //x position based on centre of enemy
		double yPos = (this.y+(this.ENEMYSIZE/2)); //y position based on centre of enemy
		double xPosPlayer = (Game.player.x+(Game.player.PLAYERSIZE/2)); //x position based on centre of player
		double yPosPlayer = (Game.player.y+(Game.player.PLAYERSIZE/2)); //y position based on centre of player

		//If enemy is not at the left side or right side of the screen
		if(this.x > 0 || this.x+Enemy.ENEMYSIZE < Game.WIDTH) {
			atEdge = false; //atEdge = false, enemy is not at the edge
		}

		//If enemy is at or outside the left or right side of the screen
		if(this.x <= 0 || this.x+Enemy.ENEMYSIZE >= Game.WIDTH) {
			atEdge = true; //atEdge = true, enemy is at the edge
		}

		//If the enemy is to the left of the player
		if(this.x <= xPosPlayer) {
			toLeft = true; //Enemy is to the left
			toRight = false; //Enemy is not to the right
		}

		//If the enemy is to the right of the player
		if(this.x >= xPosPlayer) {
			toLeft = false; //Enemy is not to the left
			toRight = true; //Enemy is to the right
		}
	
		//Check if the enemy is within proximity
		if(this.x >= (Game.player.x-PROXIMITY) && this.x <= (Game.player.x+Game.player.PLAYERSIZE+PROXIMITY)) {
			proximity = true; //Enemy is within proximity
		}
		else {
			proximity = false; //Enemy is not within proximity	
		}

		//Enemy Types------------------------------------------------------------------------------------------//
		//If it's a stupid enemy---------//
		if(this.type.equals("stupid")) {
			reloadTime = stupidReloadTime; //Set it's reload time
			speed = stupidSpeed; //Set it's speed

			//If enemy is within 25px either side of the player
			if(xPos > xPosPlayer-25 && xPos < xPosPlayer+25 && loaded == true && yPosPlayer>((Game.HEIGHT)/2)) {
				canFire = true; //It can fire
			}
			else{
				canFire = false; //It can't fire
			}
			//Right-------------------------------//
			if(toLeft && !proximity) {
				this.x += speed; //Move right
				dir = 1; //Positive direction (right)
			}
			//Left-------------------------------//
			if(toRight && !proximity) {
				this.x -= speed; //Move left
				dir = -1; //Negative direction (left)
			}
		}

		//If it's a normal enemy---------//
		if(this.type.equals("normal")) {
			canFire = true; //It can fire whenever it is reloaded
			reloadTime = normalReloadTime; //Set it's reload time
			speed = normalSpeed; //Set it's speed

			//If the enemy reaches the left side of the window
			if(this.x == 0) {
				dir = +1; //Positive direction (right)
			}
			//If the enemy reaches the right side of the window
			if(this.x+this.ENEMYSIZE == Game.WIDTH){
				dir = -1; //Negative direction (left)
			}
			//If positive dir
			if(dir == 1) {
				this.x += speed; //Move right
			}
			//If negative dir
			if(dir == -1) {
				this.x -= speed; //Move left
			}
		}

		//If it's a coordinated enemy - primarily handled with coordinatedAttack() method---------//
		if(this.type.equals("coordinated")) {
			reloadTime = coordinatedReloadTime; //Set it's reload time
			speed = coordinatedSpeed; //Set it's speed
		}

		//If it's an evasive enemy---------//
		if(this.type.equals("evasive")) {
			reloadTime = evasiveReloadTime; //Set it's reload time
			speed = evasiveSpeed; //Set it's speed

			//Left--------------------------//
			//If enemy is to the left of the player, not in proximity, at the edge, and not fleeing (If enemy hits left edge)
			if(toLeft && !proximity && atEdge && !fleeRight && !fleeLeft) {
				this.x = 0; //Set enemy position to left of screen
			}
			
			//If enemy is to the left of the player, in proximity, and at the edge
			if(toLeft && proximity && atEdge) {
				fleeRight = true; //Fleeing right
				fleeLeft = false; //Not fleeing left
			}

			//If enemy is to the left of the player, in proximity, not at edge, and not fleeing
			if(toLeft && proximity && !atEdge && !fleeRight && !fleeLeft) {
				fleeLeft = true; //Fleeing left
			}

			//Right-------------------------//
			//If enemy is to the right of the player, not in proximity, and at the edge (If enemy hits right edge)
			if(toRight && !proximity && atEdge) {
				this.x = Game.WIDTH-Enemy.ENEMYSIZE; //Set enemy position to the right edge of screen
			}

			//If enemy is to the right of the player, in proximity, and at edge
			if(toRight && proximity && atEdge) {
				fleeLeft = true; //Fleeing left
				fleeRight = false; //Not fleeing right
			}

			//If enemy is to the right of the player, in proximity, not at edge, and not fleeing
			if(toRight && proximity && !atEdge && !fleeRight && !fleeLeft) {
				fleeRight = true; //Fleeing right
			}
		
			//Left fleeing------//
			//If the enemy is fleeing left
			if(fleeLeft) {
				this.x -= speed; //Flee to the left
				canFire = false; //If it's moving, it can't fire
				dir = -1; //Negative direction (left)
			}

			//If the enemy is fleeing left, to the left of the player, and out of proximity
			if(fleeLeft && toLeft && !proximity) {
				fleeLeft = false; //Stop flee left mode
			}

			//Right fleeing------//
			//If the enemy is fleeing right
			if(fleeRight) {
				this.x += speed; //Flee to the right
				canFire = false; //If it's moving, it can't fire
				dir = 1; //Positive direction (right)
			}

			//If the enemy is fleeing right, to the right of the player, and out of proximity
			if(fleeRight && toRight && !proximity) {
				fleeRight = false; //Stop flee right mode
			}

			//Not fleeing---------//
			if(!fleeLeft && !fleeRight) {
				canFire = true; //If it isn't fleeing, it can fire
			}
		}

		//If it's a smart enemy---------//
		if(this.type.equals("smart")) {
			canFire = true; //Can fire whenever it is loaded
			reloadTime = smartReloadTime; //Set it's reload time
			speed = smartSpeed; //Set it's speed

			//Horizontal movement
			if(xPos < (xPosPlayer-50)) {
				this.x += speed; //Move right at normalSpeed
				dir = 1; //Positive direction (right)
			}
			if(xPos > (xPosPlayer+50)) {
				this.x -= speed; //Move left at normalSpeed
				dir = -1; //Negative movement (negative)
			}
		}

		//If direction is negative
		if(dir == -1) {
			left = true; //Facing left
			right = false; //Not facing right
		}

		//If direction is positive
		if(dir == 1) {
			right = true; //Facing right
			left = false; //Not facing left
		}
	}

	//coordinatedAttack method - Accepts two enemies, and coordinates an attack between the two of them
	public void coordinatedAttack(Enemy a, Enemy b) {
		//If coordinated attack is not taking place, initiate coordinated attack
		if(coordAttackingMovement) {
			//If the enemy is to the right of the player's left side-200
			if(a.x >= Game.player.x-200) {
				a.x -= a.speed; //Move left
				a.dir = -1; //Negative direction (left)
			}

			//If the enemy is to the left of the player's left side-400
			if(a.x <= Game.player.x-400) {
				a.x += a.speed; //Move right
				a.dir = 1; //Positive direction (right)
			}

			//If the enemy is to the left of the player's left side-200 and to the right of the player's left side-400 and at the edge
			if(a.x <= Game.player.x-200 && a.x >= Game.player.x-400 || a.atEdge) {
				a.canFire = true; //The enemy can fire
			}
			else {
				a.canFire = false; //The enemy can't fire
			}

			//If the enemy is to the left of the player's right side+200
			if(b.x <= Game.player.x+Game.player.PLAYERSIZE+200) {
				b.x += b.speed; //Move right
				b.dir = 1; //Positive direction (right)
			}

			//If the enemy is to the right of the player's right side+400
			if(b.x >= Game.player.x+Game.player.PLAYERSIZE+400) {
				b.x -= b.speed; //Move left
				b.dir = -1; //Negative direction (left)
			}

			//If the enemy is to the right of the player's right side+200 and to the left of the player's right side+400 and at the edge
			if(b.x >= Game.player.x+Game.player.PLAYERSIZE+200 && b.x <= Game.player.x+Game.player.PLAYERSIZE+400 || b.atEdge) {
				b.canFire = true; //The enemy can fire
			}
			else {
				b.canFire = false; //The enemy can't fire
			}

			//If both enemies can fire
			if(a.canFire && b.canFire) {
				coordAttackingMovement = false; //They cannot move
			}
		}

		//If both enemies can fire and not move
		if(a.canFire && b.canFire && !coordAttackingMovement) {
			Game.level.attack(a, "homing"); //Fire a homing missile
			Game.level.attack(b, "homing"); //Fire a homing missile
		}
		
		//If both enemies are not loaded and cannot fire
		if(!a.loaded && !b.loaded && !a.canFire && !b.canFire) {
			coordAttackingMovement = true; //Both enemies can move again in coordinated attack
		}
	}

	//render() method - Renders the enemy sprite as a graphics object
	public void render(Graphics g) {
		//If enemy is moving right
		if(right) {
			g.drawImage(Texture.enemy[0], (int)x, (int)y, width, height, null); //Draw sprite as normal (right facing)
		}
		//Otherwise, the enemy is moving left
		else {
			g.drawImage(Texture.enemy[0], (int)x+ENEMYSIZE, (int)y, -width, height, null); //Draw sprite flipped (left facing)
		}
	}
}