/**  Cave Game  Program Code
Copyright (c) 1999 James M. Bieman

To compile: javac CaveGame.java
To run:     java CaveGame
The main routine is CaveGame.main
			    
The CaveGame is a Java implementation of the old text based
adventure game from long ago.  The design was adapted from
one in Gamma, Helm, Johnson, Vlissides (The Gang of Four),
"Design Patterns: Elements of Reusable Object-Oriented Software",
Addison-Wesley, 1997.

To really be consistent with the old game we would need a
much larger cave system with a hundred or so rooms, and a 
more "understanding" user interface.

The old game just put you near the cave, displayed the "view"
as text, and offered no instructions.  If you gave a command that
it understood, you could proceed.  If your command could not
be interpreted, nothing would happen.  Rooms were never identified
precisely; your only clues came from the descriptions.  You would
have to remember or create your own map of the cave system to 
find your way around.  Sometimes you could not return exactly
the way you came.  An exit to the east may not enter the west
side of the "adjacent room"; the passage might curve.

Perhaps, this implementation can evolve to be closer to
the original game, or even go beyond it. 

Jim Bieman
September 1999.
------------------------------
Cave Game Program Modification
------------------------------
Name: David A. Becker
Date: February 20, 2008
Class: CS 314
Assignment 2

This program is a modification of the original program written by Jim Beiman.
This was an assignment for our class to complete by the date above.  

Modifications:
CaveGame.java is no longer used as the primary class.  Instead, CaveGameView and
CaveGameModel have replaced it to form a GUI representation of the game.  

Many of the original classes are still entirely intact, minus a few changes that make
messages originally printed out to the console print out onto the GUI.

Instead of printing to the console for the status messages (i.e. "Ouch, you've hit a wall", 
"You need a key to open this door", etc. I've modified those methods to return a number
based on the outcome of the action.  Each number has a message assigned to it in the 
CaveGameView class, and the new status text field i've created will display the message.
**/

package a2;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;
import javax.swing.border.Border;

public class CaveGameView extends JApplet implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//CaveGameModel
	CaveGameModel model = new CaveGameModel();
	Vector roomsVisited = new Vector();
	//count for saves
	long count = 0;
	
	//Layout Panels
	JPanel pnl_i = new JPanel();			//Intro panel
	JPanel pnl_g = new JPanel();			//New Game config
	JPanel pnl_n = new JPanel();			//North
	JPanel pnl_e = new JPanel();			//East
	JPanel pnl_w = new JPanel();			//West
	JPanel pnl_s = new JPanel();			//South
	JPanel pnl_c = new JPanel();			//Center
	
	//North Panels
	JPanel pnl_n_title = new JPanel();		//CaveGame title
	Border bdr_ttl = BorderFactory.createLineBorder(new Color(0,0,0));
	
	//South Panels
	
	//Nothing here
	
	//Left Panels
	JPanel pnl_w_char = new JPanel();		//Character
	JPanel pnl_w_hp = new JPanel();			//Character Hit Points
	JPanel pnl_w_items = new JPanel();		//Character Items
	JPanel pnl_w_itemslist = new JPanel();
	Image character;						//Character Image
	
	//Right Panels
	JPanel pnl_e_opp = new JPanel();		//Opponent
	JPanel pnl_e_hp = new JPanel();			//Opponent Hit Points
	JPanel pnl_e_items = new JPanel();		//Opponent Items
	JPanel pnl_e_itemslist = new JPanel();
	Image opponent;							//Opponent Image
	
	//Center Panels
	JPanel pnl_c_room = new JPanel();		//Room Description
	JPanel pnl_c_status = new JPanel();		//Game Status
	JPanel pnl_c_items = new JPanel();		//Room item(s)
	JPanel pnl_c_nav = new JPanel();		//Navigation
	JTextArea room_desc;
	
	//Buttons that have action listener
	JButton btn_new;
	JButton btn_load;
	JButton attack;
	JButton quit;
	JButton load;
	JButton main;
	JButton saveQuit;
	
	JComboBox gamelist;
	
	
	//Direction buttons
	JButton btn_north, btn_south,
			btn_west, btn_east,
			btn_up, btn_down;
	
	//Radio Buttons
	JRadioButton	easy, hard;
	JButton			char1, char2, char3;
	
	//Item Buttons
	JButton			btn_item1, btn_item2, btn_item3, btn_item4,
					r_btn_item1, r_btn_item2, r_btn_item3, r_btn_item4;
	
	//Character Attributes
	String char_selection;
	int warriorHealth;
	
	//Difficulty Level
	static String diff_level = "easy";
	
	//Text Areas
	JTextArea status_desc;
	
	
	public void init() {		
		//Set size and layout
		resize(1000,750);
		setLayout(new BorderLayout());
		setBackground(new Color(0,0,0));
		
		//Set All Panels Background to Black
		pnl_n.setBackground(new Color(0,0,0));
		pnl_s.setBackground(new Color(0,0,0));
		pnl_w.setBackground(new Color(0,0,0));
		pnl_e.setBackground(new Color(0,0,0));
		pnl_c.setBackground(new Color(0,0,0));

		startIntro();
		
		//Add panels to layout
		add(pnl_n, BorderLayout.NORTH);
		add(pnl_s, BorderLayout.SOUTH);
		add(pnl_w, BorderLayout.WEST);
		add(pnl_e, BorderLayout.EAST);
		add(pnl_c, BorderLayout.CENTER);
		
	}
	
	//Start Intro
	public void startIntro() {
		setupNorthPanel();
		
		pnl_i.setBorder(bdr_ttl);
		pnl_i.setLayout(new BorderLayout());
		pnl_i.setBackground(new Color(0,0,0));
		
		//Intro Description
		JLabel introdesc = new JLabel("<html><font color=White>" +
				"Welcome to One Eyed Willy's Lost Treasure!<br>" +
				"Choose either to start a new game or load from a previously saved game!");
		
		//New / Load Game Panel
		JPanel pnl_i_options = new JPanel();
		pnl_i_options.setBackground(new Color(0,0,0));
		btn_new = new JButton("New Game");
		btn_load = new JButton("Load Game");
		btn_new.addActionListener(this);
		btn_load.addActionListener(this);
		pnl_i_options.add(btn_new);
		pnl_i_options.add(btn_load);
		
		pnl_i.add(introdesc, BorderLayout.NORTH);
		pnl_i.add(pnl_i_options, BorderLayout.SOUTH);
		
		pnl_c.add(pnl_i, BorderLayout.CENTER);

	}
	
	//Setup New Game
	public void configureNewGame() {
		
		//Create configuration options panel
		pnl_g.setBorder(bdr_ttl);
		pnl_g.setBackground(new Color(0,0,0));
		pnl_g.setLayout(new BorderLayout());
		
		//Difficulty and Character Panels
		JPanel pnl_config_diff = new JPanel();
		JPanel pnl_config_char = new JPanel();
		pnl_config_diff.setBackground(new Color(0,0,0));
		pnl_config_char.setBackground(new Color(0,0,0));
		pnl_config_diff.setLayout(new BorderLayout());
		pnl_config_char.setLayout(new BorderLayout());
		
			//Difficulty Panel
			JLabel choosediff = new JLabel("<html><font color=White size=+2>" +
					"Choose Difficulty<br>");
			ButtonGroup grp_diff = new ButtonGroup();
			easy = new JRadioButton("<html><font color=White>Easy");
			easy.setBackground(new Color(0,0,0));
			hard = new JRadioButton("<html><font color=White>Hard");
			hard.setBackground(new Color(0,0,0));
			grp_diff.add(easy);
			grp_diff.add(hard);
			easy.setSelected(true);
			JPanel options = new JPanel();
			options.setBackground(new Color(0,0,0));
			options.add(easy);
			options.add(hard);
			pnl_config_diff.add(choosediff, BorderLayout.NORTH);
			pnl_config_diff.add(options, BorderLayout.SOUTH);
		
			//Character Panel
			JLabel choosechar = new JLabel("<html><font color=White size=+2>" +
					"Choose Character");
			Image img_char1 = getImage(getCodeBase(), "chunk1.jpg");
			char1 = new JButton(new ImageIcon(img_char1));
			char1.setBorderPainted(false);
			char1.setContentAreaFilled(false);
			char1.addActionListener(this);
			Image img_char2 = getImage(getCodeBase(), "chick1.jpg");
			char2 = new JButton(new ImageIcon(img_char2));
			char2.setBorderPainted(false);
			char2.setContentAreaFilled(false);
			char2.addActionListener(this);
			Image img_char3 = getImage(getCodeBase(), "data1.jpg");
			char3 = new JButton(new ImageIcon(img_char3));
			char3.addActionListener(this);
			char3.setBorderPainted(false);
			char3.setContentAreaFilled(false);
			pnl_config_char.add(choosechar, BorderLayout.NORTH);
			pnl_config_char.add(char1, BorderLayout.WEST);
			pnl_config_char.add(char2, BorderLayout.CENTER);
			pnl_config_char.add(char3, BorderLayout.EAST);
			
		//Add to config panel
		pnl_g.add(pnl_config_diff, BorderLayout.NORTH);
		pnl_g.add(pnl_config_char, BorderLayout.SOUTH);
		
		//Add to main panel		
		pnl_c.remove(pnl_i);
		pnl_c.add(pnl_g);
		pnl_c.updateUI();
	}
	
	//Start Game
	public void startGame() {
		pnl_c.removeAll();
		setupWestPanel();
		setupEastPanel();
		setupCenterPanel();
		setupSouthPanel();	
	}
	
	//Setup North Panel
	public void setupNorthPanel() {
		pnl_n.setLayout(new BorderLayout());
		
		pnl_n.setBorder(bdr_ttl);
		
		Image title = getImage(getCodeBase(), "title.png");
		JLabel lbl_title = new JLabel(new ImageIcon(title));
		
		pnl_n_title.setBackground(new Color(0,0,0) );
		pnl_n_title.add(lbl_title);
		
		pnl_n.add(pnl_n_title, BorderLayout.CENTER);
	}
	
	//West Panel: Character
	public void setupWestPanel() {
		//Main Panel
		pnl_w.setLayout(new BorderLayout());
		pnl_w.setBorder(bdr_ttl);
		
		//Character Image Panel
		pnl_w_char.setLayout(new BorderLayout());
		pnl_w_char.setBackground(new Color(0,0,0));
		


		//Hit Points Panel
		pnl_w_hp.setLayout(new BorderLayout());
		pnl_w_hp.setBackground(new Color(0,0,0));			
		
		//Items Panel
		pnl_w_items.setBackground(new Color(0,0,0));
		pnl_w_items.setLayout(new BorderLayout());
		pnl_w_itemslist.setLayout(new GridLayout(2,2,10,10));
		pnl_w_itemslist.setBackground(new Color(0,0,0));
		
			//Items Label
			JLabel lbl_w_items = new JLabel("<html><font color=White face=Verdana size=+1>" +
												"<b>Items</b>");
			
			displayPlayerInfo();
			
			//Add to Items Panel
			pnl_w_items.add(lbl_w_items, BorderLayout.NORTH);
			pnl_w_items.add(pnl_w_itemslist, BorderLayout.CENTER);
					
		//Add to Main West Panel
		pnl_w.add(pnl_w_char, BorderLayout.NORTH);
		pnl_w.add(pnl_w_hp, BorderLayout.CENTER);
		pnl_w.add(pnl_w_items, BorderLayout.SOUTH);
		
	}
	
	//East Panel: Opponent
	public void setupEastPanel() {
		//Main Panel
		pnl_e.setLayout(new BorderLayout());
		pnl_e.setBorder(bdr_ttl);
		
		//Character Image Panel
		pnl_e_opp.setLayout(new BorderLayout());
		pnl_e_opp.setBackground(new Color(0,0,0));
		


		//Hit Points Panel
		pnl_e_hp.setLayout(new BorderLayout());
		pnl_e_hp.setBackground(new Color(0,0,0));			
		
		//Items Panel
		pnl_e_items.setBackground(new Color(0,0,0));
		pnl_e_items.setLayout(new BorderLayout());
		pnl_e_itemslist.setLayout(new GridLayout(2,2,10,10));
		pnl_e_itemslist.setBackground(new Color(0,0,0));
		
			//Items Label
			JLabel lbl_e_items = new JLabel("<html><font color=White face=Verdana size=+1>" +
												"<b>Items</b>");
			
			displayWarriorInfo();
			
			//Add to Items Panel
			pnl_e_items.add(lbl_e_items, BorderLayout.NORTH);
			pnl_e_items.add(pnl_e_itemslist, BorderLayout.CENTER);
					
		//Add to Main West Panel
		pnl_e.add(pnl_e_opp, BorderLayout.NORTH);
		pnl_e.add(pnl_e_hp, BorderLayout.CENTER);
		pnl_e.add(pnl_e_items, BorderLayout.SOUTH);
		
	}
	
	//Center Panel
	public void setupCenterPanel() {
		
		pnl_c.setBackground(new Color(0,0,0));
		pnl_c.setLayout(new BorderLayout());
		
		JPanel pnl_c_n = new JPanel();
		pnl_c_n.setBackground(new Color(0,0,0));
		pnl_c_n.setLayout(new BorderLayout());
		
		//Room description panel
		pnl_c_room.setBackground(new Color(0,0,0));
		pnl_c_room.setLayout(new BorderLayout());
		
			//Room description title
			JLabel room_title = new JLabel("<html><font color=white size=+1 face=Times New Roman>" +
											"Room Description");
			room_desc = new JTextArea(8,40);
			room_desc.setLineWrap(true);
			room_desc.setEditable(false);
			room_desc.setText(model.getView());
			
			//Add to room panel
			pnl_c_room.add(room_title, BorderLayout.NORTH);
			pnl_c_room.add(room_desc, BorderLayout.CENTER);
			
		//Status panel
		pnl_c_status.setBackground(new Color(0,0,0));
		pnl_c_status.setLayout(new BorderLayout());
		
			//Status Description title
			JLabel room_status = new JLabel("<html><font color=white size=+1>Game Status");
			status_desc = new JTextArea(6,6);
			status_desc.setLineWrap(true);
			status_desc.setText("You are at the beginning of the cave!");
			
			//Add to status panel
			pnl_c_status.add(room_status, BorderLayout.NORTH);
			pnl_c_status.add(status_desc, BorderLayout.SOUTH);
			
		pnl_c_n.add(pnl_c_room, BorderLayout.NORTH);
		pnl_c_n.add(pnl_c_status, BorderLayout.SOUTH);
		
		//Room items panel
		pnl_c_items.setLayout(new GridLayout(1,4));
		pnl_c_items.setBackground(new Color(0,0,0));
		
		//Set up items in room
		displayRoomItems();
			
		//Navigation Panel
		JPanel pnl_c_navcont = new JPanel();
		pnl_c_navcont.setLayout(new BorderLayout());
		pnl_c_navcont.setBackground(new Color(0,0,0));
		JPanel pnl_c_ud = new JPanel();
		pnl_c_ud.setLayout(new GridLayout(2,1));
		pnl_c_ud.setBackground(new Color(0,0,0));
		pnl_c_nav.setLayout(new BorderLayout());
		pnl_c_nav.setBackground(new Color(0,0,0));
		
		//Navigation Images
		Image compass = getImage(getCodeBase(), "compass.jpg");
		Image up = getImage(getCodeBase(), "up.jpg");
		Image down = getImage(getCodeBase(), "down.jpg");
		Image north = getImage(getCodeBase(), "north.jpg");
		Image south = getImage(getCodeBase(), "south.jpg");
		
			//Navigation buttons
			btn_north = new JButton(new ImageIcon(north));
			btn_north.addActionListener(this);
			btn_east = new JButton("<html><font face=Verdana color=White>EAST");
			btn_east.addActionListener(this);
			btn_west = new JButton("<html><font face=Verdana color=White>WEST");
			btn_west.addActionListener(this);
			btn_south = new JButton(new ImageIcon(south));
			btn_south.addActionListener(this);
			btn_up = new JButton(new ImageIcon(up));
			btn_up.addActionListener(this);
			btn_down = new JButton(new ImageIcon(down));
			btn_down.addActionListener(this);
			JButton comp = new JButton(new ImageIcon(compass));
			
			btn_north.setBorderPainted(false);
			btn_north.setContentAreaFilled(false);	
			btn_east.setBorderPainted(false);
			btn_east.setContentAreaFilled(false);	
			btn_west.setBorderPainted(false);
			btn_west.setContentAreaFilled(false);	
			btn_south.setBorderPainted(false);
			btn_south.setContentAreaFilled(false);	
			btn_up.setBorderPainted(false);
			btn_up.setContentAreaFilled(false);		
			btn_down.setBorderPainted(false);
			btn_down.setContentAreaFilled(false);		
			comp.setBorderPainted(false);
			comp.setContentAreaFilled(false);			
			
			pnl_c_nav.add(btn_north, BorderLayout.NORTH);	
			pnl_c_nav.add(btn_west, BorderLayout.WEST);
			pnl_c_nav.add(comp, BorderLayout.CENTER);
			pnl_c_nav.add(btn_east, BorderLayout.EAST);
			pnl_c_nav.add(btn_south, BorderLayout.SOUTH);
			JPanel pnl_c_nav_c = new JPanel();
			pnl_c_nav_c.setBackground(new Color(0,0,0));
			pnl_c_nav_c.add(pnl_c_nav);
			
			pnl_c_ud.add(btn_up);
			pnl_c_ud.add(btn_down);
			
			pnl_c_navcont.add(pnl_c_ud, BorderLayout.WEST);
			pnl_c_navcont.add(pnl_c_nav_c, BorderLayout.EAST);
			JPanel pnl_c_navcont_c = new JPanel();
			pnl_c_navcont_c.setBackground(new Color(0,0,0));
			pnl_c_navcont_c.add(pnl_c_navcont);
			
		//Add to Center Panel
		pnl_c.add(pnl_c_n, BorderLayout.NORTH);
		pnl_c.add(pnl_c_items, BorderLayout.CENTER);
		pnl_c.add(pnl_c_navcont_c, BorderLayout.SOUTH);
	}
	
	//South Panel
	public void setupSouthPanel() {
		pnl_s.setBackground(new Color(0,0,0));
	}

	/**
	 * Display Attack Button
	 */
	public void displayAttackButton() {
		if(model.isWarrior() && model.getWarriorHealth() > 0){
			JPanel pnl_attack = new JPanel();
			pnl_attack.setBackground(new Color(0,0,0));
			attack = new JButton("Attack");
			attack.addActionListener(this);
			pnl_attack.add(attack);
			pnl_e_hp.add(pnl_attack, BorderLayout.CENTER);
		}
	}
	public void displaySaveButton() {
		if(!model.isWarrior() || (model.isWarrior() && model.getWarriorHealth() <= 0)){
			JPanel pnl_save = new JPanel();
			pnl_save.setBackground(new Color(0,0,0));
			saveQuit = new JButton("Save and Quit");
			saveQuit.addActionListener(this);
			pnl_save.add(saveQuit);
			pnl_e_hp.add(pnl_save, BorderLayout.CENTER);
		}
	}
	/**
	 * Display Save and Quit Page
	 */
	public void displaySaveQuitPage()
	{
		
		pnl_e.removeAll();
		pnl_w.removeAll();
		pnl_c.removeAll();
		pnl_s.removeAll();
		
		JLabel tryagain = new JLabel("Info Saved!");
		
		quit = new JButton("Close");
		quit.addActionListener(this);
		count = (System.currentTimeMillis() % 1000);
		JPanel pnl1 = new JPanel();
		JPanel pnl2 = new JPanel();
		JPanel pnl3 = new JPanel();
		JPanel pnl4 = new JPanel();
		JPanel pnl5 = new JPanel();
		JPanel pnl6 = new JPanel();
		pnl1.setBackground(new Color(0,0,0));
		pnl2.setBackground(new Color(0,0,0));
		pnl3.setBackground(new Color(0,0,0));
		pnl3.setLayout(new BorderLayout());
		pnl4.setBackground(new Color(0,0,0));
		pnl5.setBackground(new Color(0,0,0));
		pnl6.setBackground(new Color(0,0,0));
		pnl4.setLayout(new BorderLayout());
		JLabel dead = new JLabel("<html><font color=White size=+10>Your game was saved to SavedGame" + count);
		String win_image = "title2.png";
		Image winPic = getImage(getCodeBase(), win_image);
		JLabel deadguy = new JLabel(new ImageIcon(winPic));
		pnl1.add(deadguy);
		pnl2.add(dead);
		pnl5.add(tryagain);
		pnl6.add(quit);
		pnl4.add(pnl5, BorderLayout.NORTH);
		pnl4.add(pnl6, BorderLayout.SOUTH);
		pnl3.add(pnl1, BorderLayout.CENTER);
		pnl3.add(pnl2, BorderLayout.NORTH);
		pnl3.add(pnl4, BorderLayout.SOUTH);
		pnl_c.add(pnl3, BorderLayout.CENTER);
		pnl_e.updateUI();
		pnl_w.updateUI();
		pnl_c.updateUI();
		pnl_s.updateUI();
		model.saveGame(count, char_selection, diff_level);
	}
	/**
	 * Display Load Screen
	 */
	public void displayLoadScreen()
	{
		loadParse lp = new loadParse();
		lp.parseGames();
		Vector games = lp.getGames();
		
		pnl_e.removeAll();
		pnl_w.removeAll();
		pnl_c.removeAll();
		pnl_s.removeAll();
		JLabel tryagain = new JLabel("<html><font color=White>Load Saved Game");
		
		load = new JButton("Load");
		load.addActionListener(this);
		
		//List of Files
		gamelist = new JComboBox(games);
		
		JPanel pnl1 = new JPanel();
		JPanel pnl2 = new JPanel();
		JPanel pnl3 = new JPanel();
		JPanel pnl4 = new JPanel();
		JPanel pnl5 = new JPanel();
		JPanel pnl6 = new JPanel();
		pnl1.setBackground(new Color(0,0,0));
		pnl2.setBackground(new Color(0,0,0));
		pnl3.setBackground(new Color(0,0,0));
		pnl3.setLayout(new BorderLayout());
		pnl4.setBackground(new Color(0,0,0));
		pnl5.setBackground(new Color(0,0,0));
		pnl6.setBackground(new Color(0,0,0));
		pnl4.setLayout(new BorderLayout());
		JLabel dead = new JLabel("<html><font color=White size=+1>Choose what saved game you'd like to load");
		pnl1.add(gamelist);
		pnl2.add(dead);
		pnl5.add(tryagain);
		pnl6.add(load);
		pnl4.add(pnl5, BorderLayout.NORTH);
		pnl4.add(pnl6, BorderLayout.SOUTH);
		pnl3.add(pnl1, BorderLayout.CENTER);
		pnl3.add(pnl2, BorderLayout.NORTH);
		pnl3.add(pnl4, BorderLayout.SOUTH);
		pnl_c.add(pnl3, BorderLayout.CENTER);
		pnl_e.updateUI();
		pnl_w.updateUI();
		pnl_c.updateUI();
		pnl_s.updateUI();
	}	
	/**
	 * Display Winner Screen
	 */
	public void displayEndGame()
	{
			pnl_e.removeAll();
			pnl_w.removeAll();
			pnl_c.removeAll();
			pnl_s.removeAll();
			
			JLabel tryagain = new JLabel("You Fricken Won!!!");
			quit = new JButton("End Game");
			quit.addActionListener(this);
			
			JPanel pnl1 = new JPanel();
			JPanel pnl2 = new JPanel();
			JPanel pnl3 = new JPanel();
			JPanel pnl4 = new JPanel();
			JPanel pnl5 = new JPanel();
			JPanel pnl6 = new JPanel();
			pnl1.setBackground(new Color(0,0,0));
			pnl2.setBackground(new Color(0,0,0));
			pnl3.setBackground(new Color(0,0,0));
			pnl3.setLayout(new BorderLayout());
			pnl4.setBackground(new Color(0,0,0));
			pnl5.setBackground(new Color(0,0,0));
			pnl6.setBackground(new Color(0,0,0));
			pnl4.setLayout(new BorderLayout());
			JLabel dead = new JLabel("<html><font color=White size=+10>YOU ESCAPED THE CAVE!!!");
			String win_image = "waterfall.jpg";
			Image winPic = getImage(getCodeBase(), win_image);
			JLabel deadguy = new JLabel(new ImageIcon(winPic));
			pnl1.add(deadguy);
			pnl2.add(dead);
			pnl5.add(tryagain);
			pnl6.add(quit);
			pnl4.add(pnl5, BorderLayout.NORTH);
			pnl4.add(pnl6, BorderLayout.SOUTH);
			pnl3.add(pnl1, BorderLayout.CENTER);
			pnl3.add(pnl2, BorderLayout.NORTH);
			pnl3.add(pnl4, BorderLayout.SOUTH);
			pnl_c.add(pnl3, BorderLayout.CENTER);
			pnl_e.updateUI();
			pnl_w.updateUI();
			pnl_c.updateUI();
			pnl_s.updateUI();
	}
	
	/**
	 * DEAD SCREEN
	 */
	public void displayDeadScreen() {
		if(model.getPlayerHealth() <= 0){
			pnl_e.removeAll();
			pnl_w.removeAll();
			pnl_c.removeAll();
			pnl_s.removeAll();
			
			JLabel tryagain = new JLabel("Please try again!");
			quit = new JButton("Quit");
			quit.addActionListener(this);
			
			JPanel pnl1 = new JPanel();
			JPanel pnl2 = new JPanel();
			JPanel pnl3 = new JPanel();
			JPanel pnl4 = new JPanel();
			JPanel pnl5 = new JPanel();
			JPanel pnl6 = new JPanel();
			pnl1.setBackground(new Color(0,0,0));
			pnl2.setBackground(new Color(0,0,0));
			pnl3.setBackground(new Color(0,0,0));
			pnl3.setLayout(new BorderLayout());
			pnl4.setBackground(new Color(0,0,0));
			pnl5.setBackground(new Color(0,0,0));
			pnl6.setBackground(new Color(0,0,0));
			pnl4.setLayout(new BorderLayout());
			JLabel dead = new JLabel("<html><font color=White size=+10>YOU ARE DEAD!");
			String char_image = char_selection + "3.jpg";
			Image deadpic = getImage(getCodeBase(), char_image);
			JLabel deadguy = new JLabel(new ImageIcon(deadpic));
			pnl1.add(deadguy);
			pnl2.add(dead);
			pnl5.add(tryagain);
			pnl6.add(quit);
			pnl4.add(pnl5, BorderLayout.NORTH);
			pnl4.add(pnl6, BorderLayout.SOUTH);
			pnl3.add(pnl1, BorderLayout.CENTER);
			pnl3.add(pnl2, BorderLayout.NORTH);
			pnl3.add(pnl4, BorderLayout.SOUTH);
			pnl_c.add(pnl3, BorderLayout.CENTER);
			pnl_e.updateUI();
			pnl_w.updateUI();
			pnl_c.updateUI();
			pnl_s.updateUI();
		}
	}
	/**
	 * Display Player Items
	 */
	public void displayPlayerInfo() {
		//Form character image name
		int char_pic = 1;
		if(model.getPlayerHealth() <= 75 && model.getPlayerHealth() > 25)
			char_pic = 2;
		if(model.getPlayerHealth() <= 25)
			char_pic = 3;
		String char_image = char_selection + char_pic + ".jpg";
		//Character Name
		JLabel lbl_w_title = new JLabel("<html><font color=White size=+1 face=Verdana>YOU");
		pnl_w_char.add(lbl_w_title, BorderLayout.NORTH);		
		//Character Image
		character = getImage(getCodeBase(), char_image);
		JLabel lbl_char = new JLabel(new ImageIcon(character));
				
			//Hit Points label
			JLabel lbl_w_hp = new JLabel("<html><font color=White face=Verdana>" +
					"<i>Hit Points:</i><br>"+model.getPlayerHealth()+"<br><br>" +
					"<i>Weapon:</i>");
			
			//Weapon
			String str_weapon = model.getWeapon();
			Image img_weapon = getImage(getCodeBase(), str_weapon+".jpg");
			JLabel btn_weapon = new JLabel(new ImageIcon(img_weapon));
			
			//Weapon strength
			JLabel lbl_strength = new JLabel("<html><font color=White face=Verdana>" 
					+ "<i>Strength:</i><br>" + model.getWeaponStrength());
			
			
		//Items
		Image item1, item2, item3, item4;
		ArrayList item_list = model.getItems();
		if(model.checkHandsEmpty()){
			item1 = getImage(getCodeBase(), "blank_item.png");
			item2 = getImage(getCodeBase(), "blank_item.png");
			item3 = getImage(getCodeBase(), "blank_item.png");
			item4 = getImage(getCodeBase(), "blank_item.png");
		}
		else {
			item1 = getImage(getCodeBase(), item_list.get(0)+".jpg");
			if(item_list.size() > 1) {item2 = getImage(getCodeBase(), item_list.get(1)+".jpg");}
			else{ item2 = getImage(getCodeBase(), "blank_item.png");}
			if(item_list.size() > 2){ item3 = getImage(getCodeBase(), item_list.get(2)+".jpg");}
			else{ item3 = getImage(getCodeBase(), "blank_item.png");}
			if(item_list.size() > 3){ item4 = getImage(getCodeBase(), item_list.get(3)+".jpg");}
			else{ item4 = getImage(getCodeBase(), "blank_item.png");}
		}			
		
		btn_item1 = new JButton(new ImageIcon(item1));
		btn_item1.setBorderPainted(false);
		btn_item1.setContentAreaFilled(false);
		if(item_list.size() > 0) {
			btn_item1.addActionListener(this);
			btn_item1.setToolTipText("Click to drop item");
		}
		btn_item2 = new JButton(new ImageIcon(item2));
		btn_item2.setBorderPainted(false);
		btn_item2.setContentAreaFilled(false);
		if(item_list.size() > 1) {
			btn_item2.addActionListener(this);
			btn_item2.setToolTipText("Click to drop item");
		}
		btn_item3 = new JButton(new ImageIcon(item3));
		btn_item3.setBorderPainted(false);
		btn_item3.setContentAreaFilled(false);
		if(item_list.size() > 2) {
			btn_item3.addActionListener(this);
			btn_item3.setToolTipText("Click to drop item");
		}
		btn_item4 = new JButton(new ImageIcon(item4));
		btn_item4.setBorderPainted(false);
		btn_item4.setContentAreaFilled(false);
		if(item_list.size() > 3) {
			btn_item4.addActionListener(this);
			btn_item4.setToolTipText("Click to drop item");
		}

		pnl_w_char.removeAll();
		pnl_w_hp.removeAll();
		pnl_w_itemslist.removeAll();

		pnl_w_char.add(lbl_w_title, BorderLayout.NORTH);
		pnl_w_char.add(lbl_char, BorderLayout.SOUTH);		
		pnl_w_hp.add(lbl_w_hp, BorderLayout.NORTH);
		pnl_w_hp.add(btn_weapon, BorderLayout.CENTER);
		pnl_w_hp.add(lbl_strength, BorderLayout.SOUTH);
		pnl_w_itemslist.add(btn_item1);
		pnl_w_itemslist.add(btn_item2);
		pnl_w_itemslist.add(btn_item3);
		pnl_w_itemslist.add(btn_item4);
		
		pnl_w_char.updateUI();
		pnl_w_hp.updateUI();
		pnl_w_itemslist.updateUI();
	}
	
	public void displayWarriorInfo() {
		//Form character image name
		String opp_desc = model.getWarriorDesc();
		int char_pic = 0;
		
		if(model.isWarrior()){
			if(diff_level == "easy"){
				if(model.getWarriorHealth() > 50)
					char_pic = 1;
				if(model.getWarriorHealth() <= 50 && model.getWarriorHealth() > 25)
					char_pic = 2;
				if(model.getWarriorHealth() <= 25)
					char_pic = 3;
			}
			else {
				if(model.getWarriorHealth() > 100)
					char_pic = 1;
				if(model.getWarriorHealth() <= 100 && model.getWarriorHealth() > 25)
					char_pic = 2;
				if(model.getWarriorHealth() <= 25)
					char_pic = 3;
			}
		}
		String char_image = opp_desc + char_pic + ".jpg";
		//Character Name
		JLabel lbl_e_title = new JLabel("<html><font color=White size=+1 face=Verdana>OPPONENT");
		pnl_e_opp.add(lbl_e_title, BorderLayout.NORTH);		
		//Opponents Image
		opponent = getImage(getCodeBase(), char_image);
		JLabel lbl_char = new JLabel(new ImageIcon(opponent));
		JLabel lbl_e_hp = new JLabel("<html><font color=White face=Verdana>No warrior in room");
		
				
			//Hit Points label
			if(model.isWarrior()){
				String hitpoints = "DEAD"; 
				if(model.getWarriorHealth() > 0){
					hitpoints = "" + model.getWarriorHealth();
				}
				lbl_e_hp = new JLabel("<html><font color=White face=Verdana>" +
						"<i>Hit Points:</i><br>"+hitpoints+"<br><br>");
			}
			
		//Items
		Image item1 = getImage(getCodeBase(), "blank_item.png"), 
		item2 = getImage(getCodeBase(), "blank_item.png"),
		item3 = getImage(getCodeBase(), "blank_item.png"),
		item4 = getImage(getCodeBase(), "blank_item.png");
		int item_list_size = 0;
		if (!(model.checkRoomEmpty())){
			if(model.isWarrior() && model.getWarriorHealth() > 0){
				ArrayList item_list = model.getRoomItems();
				item_list_size = item_list.size();
				item1 = getImage(getCodeBase(), item_list.get(0)+".jpg");
				if(item_list.size() > 1) {item2 = getImage(getCodeBase(), item_list.get(1)+".jpg");}
				else{ item2 = getImage(getCodeBase(), "blank_item.png");}
				if(item_list.size() > 2){ item3 = getImage(getCodeBase(), item_list.get(2)+".jpg");}
				else{ item3 = getImage(getCodeBase(), "blank_item.png");}
				if(item_list.size() > 3){ item4 = getImage(getCodeBase(), item_list.get(3)+".jpg");}
				else{ item4 = getImage(getCodeBase(), "blank_item.png");}
			}
		}
		
		btn_item1 = new JButton(new ImageIcon(item1));
		btn_item1.setBorderPainted(false);
		btn_item1.setContentAreaFilled(false);
		if(item_list_size > 0) btn_item1.setToolTipText("Beat Warrior to Unlock Item");
		btn_item2 = new JButton(new ImageIcon(item2));
		btn_item2.setBorderPainted(false);
		btn_item2.setContentAreaFilled(false);
		if(item_list_size > 1) btn_item2.setToolTipText("Beat Warrior to Unlock Item");
		btn_item3 = new JButton(new ImageIcon(item3));
		btn_item3.setBorderPainted(false);
		btn_item3.setContentAreaFilled(false);
		if(item_list_size > 2) btn_item3.setToolTipText("Beat Warrior to Unlock Item");
		btn_item4 = new JButton(new ImageIcon(item4));
		btn_item4.setBorderPainted(false);
		btn_item4.setContentAreaFilled(false);
		if(item_list_size > 3) btn_item4.setToolTipText("Beat Warrior to Unlock Item");

		pnl_e_opp.removeAll();
		pnl_e_hp.removeAll();
		pnl_e_itemslist.removeAll();

		displayAttackButton();
		displaySaveButton();
		pnl_e_opp.add(lbl_e_title, BorderLayout.NORTH);
		pnl_e_opp.add(lbl_char, BorderLayout.SOUTH);		
		pnl_e_hp.add(lbl_e_hp, BorderLayout.NORTH);
		pnl_e_itemslist.add(btn_item1);
		pnl_e_itemslist.add(btn_item2);
		pnl_e_itemslist.add(btn_item3);
		pnl_e_itemslist.add(btn_item4);
		
		pnl_e_opp.updateUI();
		pnl_e_hp.updateUI();
		pnl_e_itemslist.updateUI();		
	}
	
	/**
	 * Display Room Items
	 */
	public void displayRoomItems() {
		//Items
		Image item1 = getImage(getCodeBase(), "blank_item.png"), 
				item2 = getImage(getCodeBase(), "blank_item.png"),
				item3 = getImage(getCodeBase(), "blank_item.png"),
				item4 = getImage(getCodeBase(), "blank_item.png");
		int item_list_size = 0;
		if (!(model.checkRoomEmpty())){
			if(model.isWarrior() && model.getWarriorHealth() <= 0){
				ArrayList item_list = model.getRoomItems();
				item_list_size = item_list.size();
				item1 = getImage(getCodeBase(), item_list.get(0)+".jpg");
				if(item_list.size() > 1) {item2 = getImage(getCodeBase(), item_list.get(1)+".jpg");}
				else{ item2 = getImage(getCodeBase(), "blank_item.png");}
				if(item_list.size() > 2){ item3 = getImage(getCodeBase(), item_list.get(2)+".jpg");}
				else{ item3 = getImage(getCodeBase(), "blank_item.png");}
				if(item_list.size() > 3){ item4 = getImage(getCodeBase(), item_list.get(3)+".jpg");}
				else{ item4 = getImage(getCodeBase(), "blank_item.png");}
			}
			else if(model.isWarrior() == false)
			{
				ArrayList item_list = model.getRoomItems();
				item_list_size = item_list.size();
				item1 = getImage(getCodeBase(), item_list.get(0)+".jpg");
				if(item_list.size() > 1) {item2 = getImage(getCodeBase(), item_list.get(1)+".jpg");}
				else{ item2 = getImage(getCodeBase(), "blank_item.png");}
				if(item_list.size() > 2){ item3 = getImage(getCodeBase(), item_list.get(2)+".jpg");}
				else{ item3 = getImage(getCodeBase(), "blank_item.png");}
				if(item_list.size() > 3){ item4 = getImage(getCodeBase(), item_list.get(3)+".jpg");}
				else{ item4 = getImage(getCodeBase(), "blank_item.png");}
			}
		}
					
		r_btn_item1 = new JButton(new ImageIcon(item1));
		r_btn_item1.setSize(60,60);
		r_btn_item1.setBorderPainted(false);
		r_btn_item1.setContentAreaFilled(false);
		if(item_list_size > 0){
			r_btn_item1.setToolTipText("Click to pick-up item");
			r_btn_item1.addActionListener(this);
		}
		r_btn_item2 = new JButton(new ImageIcon(item2));
		r_btn_item2.setSize(60,60);
		r_btn_item2.setBorderPainted(false);
		r_btn_item2.setContentAreaFilled(false);
		if(item_list_size > 1){
			r_btn_item2.setToolTipText("Click to pick-up item");
			r_btn_item2.addActionListener(this);
		}
		r_btn_item3 = new JButton(new ImageIcon(item3));
		r_btn_item3.setSize(60,60);
		r_btn_item3.setBorderPainted(false);
		r_btn_item3.setContentAreaFilled(false);
		if(item_list_size > 2) {
			r_btn_item3.addActionListener(this);
			r_btn_item3.setToolTipText("Click to pick-up item");
		}
		r_btn_item4 = new JButton(new ImageIcon(item4));
		r_btn_item4.setSize(60,60);
		r_btn_item4.setBorderPainted(false);
		r_btn_item4.setContentAreaFilled(false);
		if(item_list_size > 3) {
			r_btn_item4.addActionListener(this);
			r_btn_item4.setToolTipText("Click to pick-up item");
		}

		pnl_c_items.removeAll();
		
		pnl_c_items.add(r_btn_item1);
		pnl_c_items.add(r_btn_item2);
		pnl_c_items.add(r_btn_item3);
		pnl_c_items.add(r_btn_item4);
		
		pnl_c_items.updateUI();
	}
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if(src==btn_new) {
			configureNewGame();
		}
		if(src==char1) {
			char_selection = "chunk";
			if(hard.isSelected()) diff_level = "hard"; 
			startGame();
		}
		if(src==char2) {
			char_selection = "chick";
			if(hard.isSelected()) diff_level = "hard"; 
			startGame();			
		}
		if(src==char3) {
			char_selection = "data";
			if(hard.isSelected()) diff_level = "hard"; 
			startGame();			
		}
		
		//Direction Movement
		
		if(src==btn_north){
			if(model.isWarrior() && model.getWarriorHealth() > 0)
			{
				status_desc.setText(model.warriorAttack());
				
			}
			else
			{
			   int status = model.goNorth();
			   if(status == 1){
				   	status_desc.setText("Ouch!  You've hit your head against the wall!  Looks like there's no door to the North...");
			   }
			   else if(status == 2){
				   status_desc.setText("You've used a key");
				   room_desc.setText(model.getView());
				   
			   }
			   else if(status == 3){
				   status_desc.setText("You must find the right key to unlock this door!");
			   }
			   else{
				   status_desc.setText("You move your way along through the North door... The room looks a bit creepy...");
				   room_desc.setText(model.getView());
				  
			   }	
			}
		   displayRoomItems();
		   displayWarriorInfo();
			displayPlayerInfo();
			displayDeadScreen();
		}
		if(src==btn_south){
			if(model.isWarrior() && model.getWarriorHealth() > 0)
			{
				status_desc.setText(model.warriorAttack());
				
			}
			else
			{
			   int status = model.goSouth();
			   if(status == 1){
				   	status_desc.setText("Ouch!  You've hit your head against the wall!  Looks like there's no door to the South...");
			   }
			   else if(status == 2){
				   status_desc.setText("You must find the right key to unlock this door!");
				   room_desc.setText(model.getView());
				   
			   }
			   else if(status == 3){
				   status_desc.setText("You don't have the key");
			   }
			   else{
				   status_desc.setText("You move your way along through the South door... The room looks a bit creepy...");
				   room_desc.setText(model.getView());
				  
			   }	
			}
		   displayRoomItems();	
		   displayWarriorInfo();
			displayPlayerInfo();
			displayDeadScreen();
		}
		if(src==btn_east){
			if(model.isWarrior() && model.getWarriorHealth() > 0)
			{
				status_desc.setText(model.warriorAttack());
				
			}
			else
			{
			   int status = model.goEast();
			   if(status == 1){
				   	status_desc.setText("Ouch!  You've hit your head against the wall!  Looks like there's no door to the East...");
			   }
			   else if(status == 2){
				   status_desc.setText("Success!  The key worked!");
				   room_desc.setText(model.getView());
			   }
			   else if(status == 3){
				   status_desc.setText("You must find the right key to unlock this door!");
			   }
			   else{
				   status_desc.setText("You move your way along through the East door... The room looks a bit creepy...");
				   if(model.getView().compareTo("You have made the exit.  There is a giant Waterfall!") == 0)
				   {
					   //you won the game!
					   displayEndGame();
				   }
				   room_desc.setText(model.getView());
			   }	
			}
		   displayRoomItems();
		   displayWarriorInfo();
			displayPlayerInfo();
			displayDeadScreen();
		}
		if(src==btn_west){
			if(model.isWarrior() && model.getWarriorHealth() > 0)
			{
				status_desc.setText(model.warriorAttack());
				
			}
			else
			{
			   int status = model.goWest();
			   if(status == 1){
				   	status_desc.setText("Ouch!  You've hit your head against the wall!  Looks like there's no door to the West...");
			   }
			   else if(status == 2){
				   status_desc.setText("Success!  The key worked!");
				   room_desc.setText(model.getView());
			   }
			   else if(status == 3){
				   status_desc.setText("You must find the right key to unlock this door!");
			   }
			   else{
				   status_desc.setText("You move your way along through the West door... The room looks a bit creepy...");
				   room_desc.setText(model.getView());
			   }	
			}
		   displayRoomItems();		
		   displayWarriorInfo();
			displayPlayerInfo();
			displayDeadScreen();
		}
		if(src==btn_up){
			if(model.isWarrior() && model.getWarriorHealth() > 0)
			{
				status_desc.setText(model.warriorAttack());
				
			}
			else
			{
			   int status = model.goUp();
			   if(status == 1){
				   	status_desc.setText("Ouch!  You've hit your head against the wall!  Looks like there's no way up...");
			   }
			   else if(status == 2){
				   status_desc.setText("Success!  The key worked!");
				   room_desc.setText(model.getView());
			   }
			   else if(status == 3){
				   status_desc.setText("You must find the right key to unlock this door!");
			   }
			   else{
				   status_desc.setText("You move your way up through the ceiling... The room looks a bit creepy...");
				   room_desc.setText(model.getView());
			   }
			}
		   displayRoomItems();	
		   displayWarriorInfo();
			displayPlayerInfo();
			displayDeadScreen();
		}
		if(src==btn_down){
			if(model.isWarrior() && model.getWarriorHealth() > 0)
			{
				status_desc.setText(model.warriorAttack());
				
			}
			else
			{
			   int status = model.goDown();
			   if(status == 1){
				   	status_desc.setText("Ouch!  You've hit your head against the wall!  Looks like there's no door through the floor...");
			   }
			   else if(status == 2){
				   status_desc.setText("Success!  The key worked!");
				   room_desc.setText(model.getView());
			   }
			   else if(status == 3){
				   status_desc.setText("You must find the right key to unlock this door!");
			   }
			   else{
				   status_desc.setText("You move your way down through the floor... The room looks a bit creepy...");
				   room_desc.setText(model.getView());
			   }
			}
		   displayRoomItems();
		   displayWarriorInfo();
			displayPlayerInfo();
			displayDeadScreen();
		}
		
		
		//Pick up and drop items
		if(src==r_btn_item1){
			model.grabItem(1);
			displayPlayerInfo();
			displayRoomItems();
		}
		if(src==r_btn_item2){
			model.grabItem(2);
			displayPlayerInfo();
			displayRoomItems();
		}
		if(src==r_btn_item3){
			model.grabItem(3);
			displayPlayerInfo();
			displayRoomItems();
		}
		if(src==r_btn_item4){
			model.grabItem(4);
			displayPlayerInfo();
			displayRoomItems();
		}
		if(src==btn_item1){
			model.dropItem(1);
			displayPlayerInfo();
			displayRoomItems();
		}
		if(src==btn_item2){
			model.dropItem(2);
			displayPlayerInfo();
			displayRoomItems();
		}
		if(src==btn_item3){
			model.dropItem(3);
			displayPlayerInfo();
			displayRoomItems();
		}
		if(src==btn_item4){
			model.dropItem(4);
			displayPlayerInfo();
			displayRoomItems();
		}
		
		//Attack System
		if(src==attack){
			status_desc.setText(model.attackWarrior());
			displayPlayerInfo();
			displayWarriorInfo();
			displayAttackButton();
			displayRoomItems();
			displayDeadScreen();
		}
		
		//Quit
		if(src==quit){
			System.exit(0);			
		}
		
		//Save and Quit
		if(src==saveQuit)
		{
			displaySaveQuitPage();
		}
		
		//Load
		if(src==btn_load){
			//model.parseSavedGames();
			//ArrayList games = model.getSavedGames();
			displayLoadScreen();
		}
		if(src==load){
			loadParse temp = new loadParse();
			temp.loadSavedGame((String)gamelist.getSelectedItem());
			Vector bob = temp.getLoadInfo();
			Vector roomLoad = temp.getLoadRoom();
			
			char_selection = (String)bob.get(0);
			diff_level = (String)bob.get(5);
			model.setPlayerInfo(bob);
			model.loadSavedRooms(roomLoad);
			displayPlayerInfo();
			
			startGame();
			status_desc.setText("You have loaded your previously saved game!");
		}
	}
}
