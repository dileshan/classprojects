package a2;

/**  Cave Game  Program Code
     Copyright (c) 1999 James M. Bieman

     To compile: javac CaveGame.java
     To run:     java CaveGame

     The main routine is CaveGame.main
				    
**/

// class Room

import java.util.Vector;
import java.util.Enumeration;

public class Room implements CaveSite {

  private String description;

  private CaveSite[] side = new CaveSite[6];

  private Vector contents = new Vector();

  Room() {
    side[0] = new Wall();
    side[1] = new Wall();
    side[2] = new Wall();
    side[3] = new Wall();
    side[4] = new Wall();
    side[5] = new Wall();
    }

  public void setDesc(String d){
    description = d;
    }

  public void setSide(int direction, CaveSite m){
   side[direction] = m;
   }

  public void addItem(Item theItem){
   contents.addElement(theItem); 
   }

  public void removeItem(Item theItem){
   contents.removeElement(theItem);
   }

  public boolean roomEmpty(){
	 return contents.isEmpty();
  }

  public Item[] getRoomContents(){
   Item[] contentsArray = new Item[contents.size()];
   contents.copyInto(contentsArray);
   return contentsArray;
  }


  public void enter(Player p) {
   p.setLoc(this); 
  }

  public void exit(int direction, Player p){
   side[direction].enter(p);
   }

  public String getDesc(){
   Enumeration roomContents = contents.elements(); 
   String contentString = "";
   while(roomContents.hasMoreElements())
     contentString = 
	contentString + ((Item) roomContents.nextElement()).getDesc() + " ";

     return description + '\n' + '\n' +
     "Room Contents: " + contentString + '\n';
   }

}

