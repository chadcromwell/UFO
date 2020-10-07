/********************************************************************************************************************
Author: Chad Cromwell
Date: November 1st, 2017
Assignment: 1
Program: SpriteScreen.java
Description: A class that splits individual frames of sprites from a sprite sheet
Methods:
		getSprite method - Used to get a subimage from the img file, spaced 512*512(Sprites are drawn in 512px squares)
********************************************************************************************************************/

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class SpriteSheet {

	private BufferedImage img; //Holds BufferedImg as img

	//SpriteSheet contstructor - Accepts a path to the sprite sheet file
	public SpriteSheet(String path) {
		try {
			img = ImageIO.read(getClass().getResource(path)); //Assign image to img
		}
		catch(IOException e) {
			System.out.println("Couldn't load the image"); //If the image can't be found or loaded, let the user know
		}
	}

	//getSprite method - Used to get a subimage from the img file, spaced 512*512(Sprites are drawn in 512px squares)
	public BufferedImage getSprite(int x, int y) {
		return img.getSubimage(x*512, y*512, 512, 512); //*512 because if you enter 0,0, index is 0,0, if you type 0,1, index will move to second row. Prevents the need for calculating a row's pixel index
	}
}