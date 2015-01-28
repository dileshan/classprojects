package T3;

import java.io.*;
import java.util.*;

import javax.tools.JavaFileManager.Location;



public class GameWorld implements Serializable {
	
	// List of Location objects
	private Vector locations;

	// List of Exit objects
	private Vector exits;

	// The current location of the player and stats of the player
	private Room currentLocation;	
	private Item[] players_stuff;
	private Item theWeapon;
	
	//Save the current state the the cave class
	private Class<? extends CaveGameModel> modelSavedClass;
	// Character width for descriptions
	private int charWidth;

	// Output stream for gaming system
	transient private WidthLimitedOutputStream output;
	
	GameWorld()
	{
		locations = new Vector();
		exits = new Vector();
		
		currentLocation = null;
	}
	
	GameWorld(int characterWidth)
	{
		//call this constructor
		this();
		
		charWidth = characterWidth;		
	}
	
	public void setPlayersWeapon(Item item)
	{
		this.theWeapon = item;
	}
	
	public Item getPlayersWeapon()
	{
		return this.theWeapon;
	}
	
	public void setPlayers_stuff(Item[] items)
	{
		this.players_stuff = items;
	}
	
	public Item[] getPlayer_stuff()
	{
		return this.players_stuff;
	}
	
	/** Returns the current location of the player */
	public Room getCurrentLocation()
	{
		return currentLocation;
	}
	
	/** Assigns a new location to the current location of the player */
	public void setCurrentLocation(Room newLocation)
	{
		currentLocation = (Room) newLocation;
	}
	
	/** Adds a new exit to the gaming system */
	public void addExit(Exit exit )
	{
		// Check if exit vector already contains exit 
		if (! exits.contains ( exit ) )
			// Exit doesn't exist, and must be added
			exits.addElement ( exit);
	}
	
	/** Adds a new location to the gaming system */
	public void addLocation( Room room )
	{
		// Check if location vector already contains location 
		if (! locations.contains ( room ) )
			// Location doesn't exist, and must be added
			locations.addElement ( room );
	}
	
	public void setCaveModel(Class<? extends CaveGameModel> class1) {
		// TODO Auto-generated method stub
		modelSavedClass = class1;
	}
	
	/** Sets the output stream for the gaming systewm */
	public void setOutputStream(OutputStream out, int width)
	{
		output = new WidthLimitedOutputStream(out, width) ;
	}
	
	/** Shows the current game location */
	/*public void showLocation()
	{
		// Show title
		output.println ( currentLocation.getTitle() );

		// Show description
		output.println ( currentLocation.getDescription() );

		output.println();

		// Show available exits		
		output.println ( "Available exits :" );

		// Traverse elements of vector
		for (Enumeration e = currentLocation.getExits().elements(); e.hasMoreElements();)
		{
			// Get next exit
			Exit an_exit = (Exit) e.nextElement();

			// Print exit to our output stream
			output.println (an_exit.toString());
		}	
	}*/

	
}
