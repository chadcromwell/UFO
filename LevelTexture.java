/********************************************************************************************************************
Author: Chad Cromwell
Date: November 29th, 2017
Assignment: 1
Program: LevelTexture.java
Description: A class that renders the map textures
Methods:
		render() method - Renders the player sprite as a graphics object
********************************************************************************************************************/

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.awt.*;

public class LevelTexture {

	BufferedImage img; //Holds BufferedImg as img

	//SpriteSheet contstructor - Accepts a path to the sprite sheet file
	public LevelTexture(String path) {
		try {
			img = ImageIO.read(getClass().getResource(path)); //Assign image to img
		}
		catch(IOException e) {
			System.out.println("Couldn't load the image"); //If the image can't be found or loaded, let the user know
		}
	}

	//render() method - Renders the player sprite as a graphics object
	public void render(Graphics g) {
		g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
	}

}