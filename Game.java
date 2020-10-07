/********************************************************************************************************************
Author: Chad Cromwell
Date: November 1st, 2017
Assignment: 1
Program: Game.java
Description: A 2D UFO game where the player must evade a barrage of missiles from a group of tanks with varying amounts of intelligence, attempts to run at a target of 60FPS
Copyright:
	Sounds: Sounds were created using Chiptone: http://sfbgames.com/chiptone Creative Commons License CC0 1.0 Universal (CC0 1.0): https://creativecommons.org/publicdomain/zero/1.0/
Methods:
		start() method - Starts the thread for the game if it isn't already running
		stop() method - Stops the thread for the game if it is running
		tick() method - What happens each frame
		render() method - Renders each frame
		run() method - The game loop
		level1() method - Loads level 1
		hp() method - Renders the HP amount of the player in the top right corner
********************************************************************************************************************/

/**********!!!ATTENTION!!!**********/
//There is currently an unresolved Priority 3 Bug in JDK running on MacOS and possibly other OSs with drawString()
//which causes a few second delay upon initialization. There is currently no work around, so please excuse the delay/white screen and speed up of the first frames during the startup of the game.
//If you'd like to see how it starts up without the bug, just comment out where hp(g) is called above and it should load properly.
//Link: https://bugs.openjdk.java.net/browse/JDK-8179209
/**********************************/

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.event.*;
import javax.swing.JFrame;
import java.io.*;

public class Game extends Canvas implements Runnable, KeyListener {
	//Finals
	public static final int WIDTH = 1024; //Window width
	public static final int HEIGHT = 768; //Window height
	public static final String TITLE = "UFO!"; //Window title
	public static final double FPS = 60.0; //Desired FPS

	//Game variables
	private boolean isRunning = false; //Boolean to keep track of whether game is running or not
	private Thread thread; //Thread
	public static Player player; //Player
	public static Level level; //Level
	public static WinScreen winScreen; //Win object
	public static DeadScreen deadScreen; //Dead object
	public static StartScreen startScreen; //StartScreen object
	static PauseScreen pauseScreen; //PauseScreen object
	public static SpriteSheet spriteSheet; //Sprites
	public String levelName = "level1"; //String to choose which level
	private Font f = new Font("Arial", Font.BOLD, 12); //Set the font
	private int w; //Holds a drawString rendered width
	boolean yes; //If user chooses yes
	boolean no; //If user chooses no
	boolean enter; //If user presses enter
	boolean paused; //If user pauses
	boolean canPress; //If user has released the escape key

	//FPS variables, initialized in run() method
	int fps; //Holds the count of the current fps
	double timer; //Holds current time in milliseconds, used to display FPS
	long lastTime; //Holds the last time the run method was called
	double targetTick; //Holds desired FPS
	double d; //Holds error amount between actual fps and desired fps
	double interval; //Interval between ticks
	long now; //Holds the current time for the new frame

	//Game constructor
	public Game(){
		Dimension dimension = new Dimension(Game.WIDTH, Game.HEIGHT); //Create dimensions for window size
		setPreferredSize(dimension); //Set the start size
		setMinimumSize(dimension); //Set the min size
		setMaximumSize(dimension); //Set the max size, this locks the window size.
		addKeyListener(this); //Add the key listener to the game object
		level1(); //Load level 1
		winScreen = new WinScreen(); //Create win object
		deadScreen = new DeadScreen(); //Create dead object
		startScreen = new StartScreen(); //Create startScreen object
		pauseScreen = new PauseScreen(); //Create pauseScreen object
		spriteSheet = new SpriteSheet("img/sprites.png"); //Create SpriteSheet object
		new Texture(); //Initialize texture object
	}

	//start() method - Starts the thread for the game if it isn't already running
	public synchronized void start() {
		if(isRunning) return; //If the game is already running, exit method
		isRunning = true; //Set boolean to true to show that it is running
		thread = new Thread(this); //Create a new thread
		thread.start(); //Start the thread
	}

	//stop() method - Stops the thread for the game if it is running
	public synchronized void stop() {
		if(!isRunning) return; //If the game is stopped, exit method
		isRunning = false; //Set boolean to false to show that the game is no longer running
		//Attempt to join thread (close the threads, prevent memory leaks)
		try {
			thread.join();
		}
		//If there is an error, print the stack trace for debugging
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	//tick() method - What happens each frame
	private void tick() {

		//If the player is not dead, has not won, and is has started the game
		if(!player.dead && !player.win && startScreen.start && !paused) {
			level.tick(); //Call level tick method
			player.tick(); //Call player tick method
		}

		//If the player is dead
		if(player.dead){
			//If the user presses yes
			if(yes) {
				player.dead = false; //The player is no longer dead
				level1(); //Load level 1
			}
			//If the user presses no
			if(no) {
				System.exit(1); //Exit
			}
		}
		//If the player won
		if(player.win) {
			//If the user presses yes
			if(yes) {
				player.win = false; //The player has no longer won
				level1(); //Load level 1
			}
			//If the user persses no
			if(no) {
				System.exit(1); //Exit
			}
		}
	}

	//render() method - Renders each frame
	private void render() {
		BufferStrategy bufferStrategy = getBufferStrategy(); //Create BufferStrategy object

		//If the Buffer Strategy doesn't exist, create one
		if(bufferStrategy == null) {
			createBufferStrategy(3); //Triple buffer
			return; //Return
		}

		Graphics g = bufferStrategy.getDrawGraphics(); //Create Graphics object
		g.setColor(Color.black); //Set the colour of the object to black
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT); //Fill the window

		//Start screen
		if(!startScreen.start && !player.dead && !player.win) {
			startScreen.render(g); //Render the start screen
			//If the user presses enter
			if(enter) {
				startScreen.start = true; //Game has started
			}
		}

		//Playing screen
		if(!player.dead && !player.win && startScreen.start) {
			level.render(g); //Render the level
			player.render(g); //Render the player to the graphics object
			hp(g); //Render the hit points
			if(paused) {
				pauseScreen.render(g);
			}
		}

		//Dead screen
		if(player.dead){
			deadScreen.render(g); //Render the dead screen
		}

		//Win screen
		if(player.win) {
			winScreen.render(g); //Render the win screen
		}
		g.dispose(); //Dispose of the object
		bufferStrategy.show(); //Show it
	}

	//run() method - The game loop
	@Override
	public void run() {
		requestFocus(); //So window is selected when it opens
		fps = 0; //Counts current fps
		timer = System.currentTimeMillis(); //Keep track of current time in milliseconds, used to display FPS
		lastTime = System.nanoTime(); //Keep track of the last time the method was called
		targetTick = FPS; //Set desired FPS
		d = 0; //Varible used to keep track if it is running at desired FPS/used to compensate
		interval = 1000000000/targetTick; //Interval between ticks

		while(isRunning) {
			now = System.nanoTime(); //Capture the time now
			d += (now - lastTime)/interval; //Calculate d
			lastTime = now; //Update lastTime

			//If d is >= 1 we need to render to stay on fps target
			while(d >= 1) {
				tick(); //Call tick method
				render(); //Call render method
				fps++; //Increment fps
				d--; //Decrement d
			}

			//If the difference between the current system time is greater than 1 second than last time check, print the fps, reset fps to 0, and increase timer by 1 second
			if(System.currentTimeMillis() - timer >= 1000) {
				fps = 0; //Set fps to 0
				timer+=1000; //Increase timer by 1 second
			}
		}
		stop(); //Stop the game
	}

	//level1() method - Loads level 1
	public void level1() {
		level = new Level(1); //Create Level object, passing 1 to constructor for level 1
		player = new Player(100, 100); //Set player y position); //Create player object, place in middle of screen, position will later be determined by level png
	}

	//!!!ATTENTION!!! There is currently an unresolved Priority 3 Bug in JDK on MacOS and possibly other OSs with drawString()
	//which causes a few second delay upon initialization. There is currently no work around, so please excuse the delay/speed up during the startup of the game.
	//If you'd like to see how it starts up without the bug, just comment out where hp(g) is called above and it should load properly.
	//Link: https://bugs.openjdk.java.net/browse/JDK-8179209
	//hp() method - Renders the HP amount of the player in the top right corner
	public void hp(Graphics g) {
		w = g.getFontMetrics().stringWidth("HP: " + player.hp); //Get the width of the rendered text
		g.setColor(Color.white); //Set the colour for the text
		g.setFont(f); //Set the font for the text
		g.drawString("HP: " + player.hp, WIDTH-w-25, 25); //Draw "HP: #" in the top right corner of the screen
	}

	//Main
	public static void main(String[] args) {
		Game game = new Game(); //Create Game object
		JFrame frame = new JFrame(); //Create new frame
		frame.setTitle(Game.TITLE); //Add the title to the frame
		frame.add(game); //Add the game class to the frame
		frame.setResizable(false); //Window cannot be resized
		frame.pack(); //Build the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Exit the program when the window is closed
		frame.setLocationRelativeTo(null); //Open the window in the middle
		frame.setVisible(true); //Make the window visible
		game.start(); //Call start method in game object, starts the game
	}

	//For key presses
	@Override
	public void keyPressed(KeyEvent e) {
		//Moving vertically
		//If W is pressed (UP)
		if(e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true; //Set up boolean to true
		}
	
		//If S is pressed (DOWN)
		if(e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true; //Set down boolean to true
		}
	
		//Moving horizontally
		//If A is pressed (LEFT)
		if(e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true; //Set left boolean to true
		}

		//If D is pressed (RIGHT)
		if(e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true; //Set right boolean to true
		}

		//If SPACE is pressed (FIRE)
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			player.space = true;
		}

		//If Y is pressed (YES)
		if(e.getKeyCode() == KeyEvent.VK_Y) {
			yes = true;
		}

		//If N is pressed (NO)
		if(e.getKeyCode() == KeyEvent.VK_N) {
			no = true;
		}

		//If ENTER is pressed (START)
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			enter = true;
		}

		//If ESCAPE is pressed (PAUSE)
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				if(!paused && canPress) {
					paused = true;
					canPress = false;
				}
				if(paused && canPress) {
					paused = false;
					canPress = false;
				}
		}
	}

	//For key releases
	@Override
	public void keyReleased(KeyEvent e) {
		//If W is released (UP)
		if(e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false; //Set up boolean to false
		}

		//If S is released (DOWN)
		if(e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false; //Set down boolean to false
		}

		//If A is released (LEFT)
		if(e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false; //Set left boolean to false
		}

		//If D is released (RIGHT)
		if(e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false; //Set right boolean to false
		}
		
		//If SPACE is released (FIRE)
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			player.space = false;
		}
		
		//If ENTER is released (ENTER)
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			enter = false;
		}

		//If Y is pressed (YES)
		if(e.getKeyCode() == KeyEvent.VK_Y) {
			yes = false;
		}

		//If N is pressed (NO)
		if(e.getKeyCode() == KeyEvent.VK_N) {
			no = false;
		}

		//If ESCAPE is pressed (PAUSE)
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			canPress = true;
		}
	}

	//keyTyped override needed, do not use
	@Override
	public void keyTyped(KeyEvent e) {

	}
}