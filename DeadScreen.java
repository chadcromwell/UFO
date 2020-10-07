/********************************************************************************************************************
Author: Chad Cromwell
Date: November 20th, 2017
Assignment: 1
Program: DeadScreen.java
Description: A class that displays the screen after the player dies
Methods:
		render() method - Accepts a graphics object and renders text to the screen
********************************************************************************************************************/

import java.awt.*;

public class DeadScreen {
	int w; //Holds width of the text
	int headingHeight; //Holds the height of the heading text
	int subHeadingHeight; //Holds the height of the sub heading text
	int tHeight; //Holds height location for line of text
	private Font heading = new Font("Arial", Font.BOLD, 24); //Set the font
	private Font subHeading = new Font("Arial", Font.BOLD, 12); //Set the font

	//render() method - Accepts a graphics object and renders text to the screen
	public void render(Graphics g) {
		headingHeight = g.getFontMetrics().getHeight(); //Get the width of the rendered text
		subHeadingHeight = g.getFontMetrics().getHeight(); //Get the width of the rendered text
		tHeight = (Game.level.height/2)-(headingHeight/2); //Update tHeight

		g.setFont(heading); //Set the font for the text
		w = g.getFontMetrics().stringWidth("You lost!"); //Get the width of the rendered text
		g.setColor(Color.white); //Set the colour for the text
		g.drawString("You lost!", (Game.level.width/2)-(w/2), tHeight); //Draw "You Lost!" cenetered

		g.setFont(subHeading); //Set the font for the text
		tHeight += subHeadingHeight; //Update tHeight
		w = g.getFontMetrics().stringWidth("Try again:"); //Get the width of the rendered text
		g.drawString("Try again:", (Game.level.width/2)-(w/2), tHeight); //Draw "Try again:" centered
		
		tHeight += subHeadingHeight;
		w = g.getFontMetrics().stringWidth("Y/N?"); //Get the width of the rendered text
		g.drawString("Y/N?", (Game.level.width/2)-(w/2), tHeight); //Draw "Y/N?" centered
	}
}