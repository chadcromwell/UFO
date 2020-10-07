/********************************************************************************************************************
Author: Chad Cromwell
Date: November 20th, 2017
Assignment: 1
Program: StartScreen.java
Description: A class that displays the start screen
Methods:
		render() method - Accepts a graphics object and renders text to the screen
********************************************************************************************************************/

import java.awt.*;

public class StartScreen {
	int w; //Holds width of the text
	int headingHeight; //Holds the height of the heading text
	int subHeadingHeight; //Holds the height of the sub heading text
	int tHeight; //Holds height location for line of text
	boolean start = false; //Whether or not to start the game
	private Font heading = new Font("Arial", Font.BOLD, 24); //Set the font
	private Font subHeading = new Font("Arial", Font.BOLD, 12); //Set the font

	//render() method - Accepts a graphics object and renders text to the screen
	public void render(Graphics g) {
		headingHeight = g.getFontMetrics().getHeight(); //Get the width of the rendered text
		subHeadingHeight = g.getFontMetrics().getHeight(); //Get the width of the rendered text
		tHeight = (Game.level.height/2)-(headingHeight/2); //Update tHeight

		g.setFont(heading); //Set the font for the text
		w = g.getFontMetrics().stringWidth("UFO!"); //Get the width of the rendered text
		g.setColor(Color.white); //Set the colour for the text
		g.drawString("UFO!", (Game.level.width/2)-(w/2), (Game.level.height/2)-(headingHeight/2)); //Draw "UFO!" centered

		g.setFont(subHeading); //Set the font for the text
		tHeight += subHeadingHeight; //Update tHeight
		w = g.getFontMetrics().stringWidth("Use WASD to move and SPACE to shoot"); //Get the width of the rendered text
		g.drawString("Use WASD to move and SPACE to shoot", (Game.level.width/2)-(w/2), tHeight); //Draw "Use WASD to move and SPACE to shoot" centered

		tHeight += subHeadingHeight; //Update tHeight
		w = g.getFontMetrics().stringWidth("Press ESCAPE to pause at any time"); //Get the width of the rendered text
		g.drawString("Press ESCAPE to pause at any time", (Game.level.width/2)-(w/2), tHeight); //Draw "Press ESCAPE to pause at any time" centered
		
		tHeight += subHeadingHeight; //Update tHeight
		w = g.getFontMetrics().stringWidth("To start press ENTER"); //Get the width of the rendered text
		g.drawString("To start press ENTER", (Game.level.width/2)-(w/2), tHeight); //Draw "To start press ENTER" centered
	}
}