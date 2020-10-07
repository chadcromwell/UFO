/********************************************************************************************************************
Author: Chad Cromwell
Date: November 1st, 2017
Assignment: 1
Program: LevelTexture.java
Description: A class that creates a tile that holds a collidable attribute, allows you to create a level with collidable tiles
********************************************************************************************************************/

import java.awt.*;

public class Tile extends Rectangle {

	int x; //x position
	int y; //y position
	boolean collide; //Collision boolean
 
	public Tile(int x, int y, boolean collide) {
		setBounds(x, y, Level.TILEPIX, Level.TILEPIX); //Set size of tile
		this.x = x;
		this.y = y;
		this.collide = collide;
	}
}