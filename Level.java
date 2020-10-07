/********************************************************************************************************************
Author: Chad Cromwell
Date: November 1st, 2017
Assignment: 1
Program: Level.java
Description: A class that loads a level for the UFO game, only used to load a single level for now. Can be adapted to load more levels in the future quite easily.
Methods:
		tick() method - What is executed each frame
		reload(Enemy a) method - Reloads the enemy. Accepts an Enemy object
		updateArrays() method - Updates the enemy arrays
		enemiesAttack() method - Handles how enemies attack
		playerAttack() method - Handles player firing laser
		animate() method - Animates the sprites
		render() method - Renders everything
********************************************************************************************************************/

import java.awt.image.*;
import java.io.*;
import java.awt.*;
import javax.imageio.*;

public class Level {
	
	//Finals
	static final double GROUNDHEIGHT = (double)Game.HEIGHT-96; //Ground height, used to place things on the ground
	static final int TILEPIX = 1; //Size of tiles, go pixel for pixel as it gives highest resolution

	//Level variables
	int width; //Width of screen
	int height; //Height of screen
	boolean change = true; //Used to determine if an array list change has taken place
	static Tile[][] borderTiles; //Array of borderTiles for holding border tiles
	String levelNumber; //Holds path to level png
	LevelTexture levelTexture; //Map textures

	//Lists to hold sprites
	java.util.List<Enemy> enemies;
	java.util.List<Missile> missiles;
	java.util.List<Laser> lasers;
	java.util.List<Explosion> explosions;
	java.util.List<Enemy> normalIndex;
	java.util.List<Enemy> coordinatedIndex;
	java.util.List<Enemy> stupidIndex;
	java.util.List<Enemy> evasiveIndex;
	java.util.List<Enemy> smartIndex;

	//Level constructor - Accepts an int to determine what level to load
	public Level(int levelNumber) {
		this.levelNumber = Integer.toString(levelNumber); //Capture levelNumber and assign it to the level object

		//Initialize Lists
		enemies = new java.util.ArrayList<>(); //Enemies list
		missiles = new java.util.ArrayList<>(); //Missiles list
		lasers = new java.util.ArrayList<>(); //Lasers list
		explosions = new java.util.ArrayList<>(); //Lasers list
		normalIndex = new java.util.ArrayList<>(); //Normal enemies list
		coordinatedIndex = new java.util.ArrayList<>(); //Coordinated enemies list
		stupidIndex = new java.util.ArrayList<>(); //Stupid enemies list
		evasiveIndex = new java.util.ArrayList<>(); //Evasive enemies list
		smartIndex = new java.util.ArrayList<>(); //Smart enemies list
		levelTexture = new LevelTexture("img/level" + levelNumber + "texture.png"); //Initialize the map texture

		try {
			BufferedImage map = ImageIO.read(getClass().getResource("img/level" + levelNumber + ".png")); //Open image and assign to map
			this.width = map.getWidth(); //Get the width of the map image, assign it to width
			this.height = map.getHeight(); //Get the height of the map image, assign it to height
			int[] pixels = new int[width*height]; //Array to hold pixels
			borderTiles = new Tile[width][height]; //Create new Tile object array, assign it to borderTiles
			map.getRGB(0, 0, width, height, pixels, 0, width); //Get RGB component of pixels from the map and assign them to the pixel array

			//Iterate through each element in pixel array by 1*1px resolution (So that level is rendered in a 1:1 resolution)
			for(int x = 0; x < width; x++) {
				for(int y = 0; y < height; y++) {
					int val = pixels[x + (y*width)];

					//If pixel is white, it's a boundary
					if(val == 0xFFFFFFFF) {
						borderTiles[x][y] = new Tile(x*TILEPIX, y*TILEPIX, true); //Create a tile in its location in the Tile array
					}
				}
			}

			//Add enemies to level (lists)
			enemies.add(new Enemy(100, GROUNDHEIGHT, "stupid"));
			enemies.add(new Enemy(200, GROUNDHEIGHT, "coordinated"));
			enemies.add(new Enemy(300, GROUNDHEIGHT, "coordinated"));
			enemies.add(new Enemy(400, GROUNDHEIGHT, "evasive"));
			enemies.add(new Enemy(500, GROUNDHEIGHT, "smart"));
		}
		catch (IOException e) {
			e.printStackTrace(); //For debugging
		}
	}

	//tick() method - What is executed each frame
	public void tick() {
		animate(); //Animate enemies
		updateArrays(); //Update the index arrays
		enemiesAttack(); //Call enemiesAttack
		playerAttack(); //Call playerAttack();
	}

	//reload(Enemy a) method - Reloads the enemy. Accepts an Enemy object
	public void reload(Enemy a) {
		//If the enemy is not loaded
		if(!a.loaded) {
			//If the amount of time to reload has passed
			if(a.reloadTimer >= a.reloadTime) {
				a.loaded = true; //Now loaded
				a.reloadTimer = 0; //Reset reloadTimer
			}
			else {
				++a.reloadTimer; //Increment reloadTimer
			}
		}
	}

	//fire(Enemy a) method - Fires the enemy weapon. Accepts an Enemy object
	public void fire(Enemy a, String type) {
		//If the enemy can fire and is loaded
		if(a.canFire && a.loaded) {
			a.loaded = false; //It's no longer loaded
			a.canFire = false; //It cannot fire anymore
			missiles.add(new Missile(a, type));	//Create a missile object (fire a missile)
		}
	}

	//attack(Enemy a) method - Causes the enemy to attack, calls reload, then fire. Accepts an Enemy object
	public void attack(Enemy a, String type) {
		reload(a); //Reload the enemy
		fire(a, type); //Fire a missile of the parameter type
	}

	//updateArrays() method - Updates the enemy arrays
	public void updateArrays() {

		//If a change in the number of enemies has taken place
		if(change) {

			//Clear the lists
			normalIndex.clear();
			coordinatedIndex.clear();
			stupidIndex.clear();
			evasiveIndex.clear();
			smartIndex.clear();

			//For each enemy in the enemy array
			for(int i = 0; i < enemies.size(); i++) {

				//If it's a normal type
				if(enemies.get(i).type == "normal") {
					normalIndex.add(enemies.get(i)); //Add it to the normal index array
				}
				
				//If it's a coordinated type
				if(enemies.get(i).type == "coordinated") {
					coordinatedIndex.add(enemies.get(i)); //Add it to the coordinated index array
				}

				//If it's a stupid type
				if(enemies.get(i).type == "stupid") {
					stupidIndex.add(enemies.get(i)); //Add it to the normal index array
				}

				//If it's a evasive type
				if(enemies.get(i).type == "evasive") {
					evasiveIndex.add(enemies.get(i)); //Add it to the normal index array
				}

				//If it's a smart type
				if(enemies.get(i).type == "smart") {
					smartIndex.add(enemies.get(i)); //Add it to the normal index array
				}
			}
			change = false; //We're done account for the change in enemies
		}
	}

	//enemiesAttack() method - Handles how enemies attack
	public void enemiesAttack() {
		//If there are stupid enemies
		if(stupidIndex.size() > 0) {
			//For each stupid enemy in the stupidIndex
			for(int i = 0; i < stupidIndex.size(); i++) {
				attack(stupidIndex.get(i), "normal"); //Call attack for each one
			}
		}

		//If there are more than 2 normal enemies
		if(coordinatedIndex.size() > 1) {
			enemies.get(0).coordinatedAttack(coordinatedIndex.get(coordinatedIndex.size()-2), coordinatedIndex.get(coordinatedIndex.size()-1)); //Call coordinated attack for 2 last enemies
			//For each normal enemy in the normalIndex, except for the last two
			for(int i = 0; i < coordinatedIndex.size()-2; i ++) {
				attack(coordinatedIndex.get(i), "coordinated"); //Call attack for each one
			}
		}

		//If there is only 1 coordinated enemy
		if(coordinatedIndex.size() == 1) {
			coordinatedIndex.get(0).type = "normal";
			normalIndex.add(coordinatedIndex.get(0));
			coordinatedIndex.clear();
		}

		if(normalIndex.size() > 0) {
			for(int i = 0; i < normalIndex.size(); i++) {
				attack(normalIndex.get(i), "normal");
			}
		}

		//If there are evasive enemies
		if(evasiveIndex.size() > 0) {
			//For each evasive enemy in the evasiveIndex
			for(int i = 0; i < evasiveIndex.size(); i++) {
				attack(evasiveIndex.get(i), "normal"); //Call attack for each one
			}
		}

		//If there are smart enemies
		if(smartIndex.size() > 0) {
			//For each smart enemy in the smartIndex
			for(int i = 0; i < smartIndex.size(); i++) {
				attack(smartIndex.get(i), "smart"); //Call attack for each one
			}
		}
	}

	//playerAttack() method - Handles player firing laser
	public void playerAttack() {
		//If player isn't loaded
		if(!Game.player.loaded) {
			//If the reload time is reached
			if(Game.player.reloadTimer >= Game.player.reloadTime) {
				Game.player.reloadTimer = 0; //Reset the reload timer to 0
				Game.player.loaded = true; //Loaded is true
			}
			//Otherwise, count until the reload timer is met
			else {
				++Game.player.reloadTimer; //Increment reload timer
			}
		}
		//If the player is loaded and space is pressed
		if(Game.player.loaded && Game.player.space) {
			lasers.add(new Laser(Game.player)); //Fire a laser
			Game.player.loaded = false; //The player is no longer loaded
		}
	}

	//animate() method - Animates the sprites
	public void animate() {
		//Animate each enemy
		for(int i = 0; i < enemies.size(); i++) {
			enemies.get(i).tick();
		}
		//Animate each missile
		for(int i = 0; i < missiles.size(); i++) {
			missiles.get(i).tick();
		}
		//Animate each laser
		for(int i = 0; i < lasers.size(); i++) {
			lasers.get(i).tick();
		}
		//Animate each explosion
		for(int i = 0; i < explosions.size(); i++) {
			explosions.get(i).tick();
		}
	}

	//render() method - Renders everything
	public void render(Graphics g) {
		//Render map textures
		levelTexture.render(g);

		//Render enemies
		for(int i = 0; i < enemies.size(); i++) {
			enemies.get(i).render(g);
		}
		//Render missiles
		for(int i = 0; i < missiles.size(); i++) {
			missiles.get(i).render(g);
		}
		//Render lasers
		for(int i = 0; i < lasers.size(); i++) {
			lasers.get(i).render(g);
		}
		//Animate each explosion
		for(int i = 0; i < explosions.size(); i++) {
			explosions.get(i).render(g);
		}
	}
}