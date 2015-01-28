package a2;

/**  Cave Game  Program Code
     Copyright (c) 1999 James M. Bieman

     To compile: javac CaveGame.java
     To run:     java CaveGame

     The main routine is CaveGame.main
				    
**/

// class Door

public class Door implements CaveSite {
  /** In this implementation doors are always locked.
      A player must have the correct key to get through
      a door.  Doors automatically lock after a player
      passes through. */

  private String myKey;

  /** The door's location. */
  private CaveSite outSite;
  private CaveSite inSite;

  /** We can construct a door at the site. */
  Door(CaveSite out, CaveSite in, String k){
    outSite = out;
    inSite = in;
    myKey = k;
  }

 /** A player will need the correct key to enter. */
  /**
   * This method has been modified to return a number based on
   * the status of the success of the door being opened.
   */
 public int enter(Player p)
 {
		Key theKey = new Key();
		theKey.setDesc(myKey);
		//System.out.println("Name: " + myKey);
		if (p.haveItem(theKey))
		{
			if (p.getLoc() == outSite) 
				inSite.enter(p);
			else if (p.getLoc() == inSite) 
				outSite.enter(p); 
			return 2;
		}
		else
		{
	       return 3;
	 	}
 }

}

