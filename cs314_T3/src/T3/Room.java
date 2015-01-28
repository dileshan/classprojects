package a2;


/**  Cave Game  Program Code
     Copyright (c) 1999 James M. Bieman

     To compile: javac CaveGame.java
     To run:     java CaveGame

     The main routine is CaveGame.main
				    
**/

// class Room

import java.util.ArrayList;
import java.util.Vector;
import java.util.Enumeration;

public class Room implements CaveSite {

  private String description;

  private CaveSite[] side = new CaveSite[6];

  private Vector contents = new Vector();
  private Warrior warrior;
  private int roomNum = 0;
  
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
  
  public void addWarrior(Warrior theWarrior){
	  warrior = theWarrior;
  }
  
  public Warrior getWarrior(){
	  return warrior;
  }
  public int getNum()
  {
	  return roomNum;
  }
  public void setRoomNum(int a)
  {
	  roomNum = a;
  }
  public boolean isWarrior(){
	  if(warrior != null)
		  return true;
	  return false;
  }

  public void removeItem(Item theItem){
   contents.removeElement(theItem);
   }
  
  public void removeAllItem(){
	   contents.clear();
	   }

  public boolean roomEmpty(){
	 return contents.isEmpty();
  }

  public Item[] getRoomContents(){
   Item[] contentsArray = new Item[contents.size()];
   contents.copyInto(contentsArray);
   return contentsArray;
  }
  
public ArrayList getRoomItemDesc(){
	Item[] contentsArray = new Item[contents.size()];
	contents.copyInto(contentsArray);
	ArrayList items = new ArrayList();
	for (int n = 0; n < contentsArray.length; n++) {
		items.add(contentsArray[n].getDesc());
	}
	return items;  
}

  /**
   * This method has been modified to return a number based on
   * the action taken.  In this instance, a location was successfully
   * changed.
   */
  public int enter(Player p) {
   p.setLoc(this);
   return 0; 
  }

  /**
   * This method has been modified to return a number based on
   * the action taken.  In this instance, it is the number of the
   * wall being hit.
   * 
   * @param direction
   * @param p
   * @return
   */
  public int exit(int direction, Player p){

	
   int status = side[direction].enter(p);
   return status;
   }

  public String getDesc(){
   Enumeration roomContents = contents.elements(); 
   String contentString = "";
   while(roomContents.hasMoreElements())
     contentString = 
	contentString + ((Item) roomContents.nextElement()).getDesc() + " ";

     return description;
   }

}

