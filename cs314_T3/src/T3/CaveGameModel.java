/**  Cave Game  Program Code
     Copyright (c) 1999 James M. Bieman

     Modified February, 2008 David A. Becker
     
     CaveGameModel
     This class relays messages between the CaveGameView (main) class and 
     the internal classes that keep track of the gameplay.
				    
**/

package a2;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

/**
 * @author Bic
 *
 */
public class CaveGameModel {

	Player thePlayer;
	caveParse theCave;
	Room startRm;
	Weapons startWeap = new Weapons();
	
	Item i = new Item();
	
  CaveGameModel() { // we initialize
	   thePlayer = new Player();
	   
	   //Initialize the player's item to a stick
	   startWeap.setDesc("stick");
	   startWeap.setStrength(20);
	   thePlayer.setWeapon(startWeap);
	   
	   theCave = new caveParse();
	   startRm = theCave.readFile();
	   thePlayer.setRoom(startRm);
  }  
  
  public void saveGame(long a, String b, String c)
  {
	  Vector temp = theCave.getSaveRooms();
	  SaveGame game_tb_saved = new SaveGame(thePlayer.getLoc(), thePlayer.getWeapon(), thePlayer.getHealth(), thePlayer.showMyThings(), theCave.roomsTot, a, b, c, temp);
  }
  	
	/**
	 * go methods
	 * This group of methods have been modified to actually
	 * make the movement with the CaveGame methods.  It will
	 * return an int based on what response should be placed 
	 * onto the GUI.
	 * 
	 * @return
	 */
	public int goUp(){
		int status = thePlayer.go(4);
		return status;
	}

	public int goDown(){
		int status = thePlayer.go(5);
		return status;
	}

  public int goNorth(){
	  int status = thePlayer.go(0);
	  return status;
    }
      
  public int goSouth(){
	  int status = thePlayer.go(1);
	  return status;
    }

  public int goEast(){
	  int status = thePlayer.go(2);
	  return status;
    }
      
  public int goWest(){
	  int status = thePlayer.go(3);
	  return status;
    }

  /**
   * getView method
   * This method has been modified to return the 
   * view of the player, an internal method.
   * 
   * @return
   */
  public String getView(){ 
	  return(thePlayer.look());
  }
  
	/**
	 * getItems method
	 * This method has been modified to return the
	 * items that the player has at the moment.
	 * 
	 * @return
	 */
	public ArrayList<String> getItems(){
		return thePlayer.showMyThings();
	}
	
	/**
	 * 
	 */
	public String getWeapon(){
		return thePlayer.getWeaponDesc();
	}


  /**
   * 
   */
  public ArrayList<String> getRoomItems() {
	  return thePlayer.getLoc().getRoomItemDesc();
  }
  public boolean checkRoomEmpty() {
	  return thePlayer.getLoc().roomEmpty();
  }
  
  /**
   * 
   */
  public boolean checkHandsEmpty() {
	  return thePlayer.handsEmpty();
  }
  
  /**
   * grabItem
   * This method will grab the item that the player specifies
   * and return a message discussing the item picked up.
   * 
   * @param theChoice
   * @return
   */
  public void grabItem(int itemChoice){
	  Item[] contentsArray = (thePlayer.getLoc()).getRoomContents();
	  
	  if(!thePlayer.handsFull()){  
		  if(contentsArray[itemChoice-1].getDesc().equals("whip") ||
				  contentsArray[itemChoice-1].getDesc().equals("gun") ||
				  contentsArray[itemChoice-1].getDesc().equals("stick") ||
				  contentsArray[itemChoice-1].getDesc().equals("lightsaber") ||
				  contentsArray[itemChoice-1].getDesc().equals("sword") ||
				  contentsArray[itemChoice-1].getDesc().equals("bomb") ||
				  contentsArray[itemChoice-1].getDesc().equals("chopsticks")){
			  Weapons w = (Weapons)thePlayer.getWeapon();
			  (thePlayer.getLoc()).removeItem(contentsArray[itemChoice-1]);
			  (thePlayer.getLoc()).addItem(thePlayer.getWeapon());
			  thePlayer.setWeapon((Weapons)contentsArray[itemChoice-1]);
		  }	 
		  else if(contentsArray[itemChoice-1].getDesc().equals("food")){
			  thePlayer.setHealth(thePlayer.getHealth() + 25);
			  thePlayer.getLoc().removeItem(contentsArray[itemChoice-1]);
		  }
		  else {
			  thePlayer.pickUp(contentsArray[itemChoice-1]);
			  (thePlayer.getLoc()).removeItem(contentsArray[itemChoice-1]);
		  }
	  }
  }
  
  /**
   * dropItem
   * This method will drop the item that the player specifies
   * and return a message if the drop was successful.
   * 
   * @param theChoice
   * @return
   */
  public void dropItem(int itemChoice){
	  thePlayer.drop(itemChoice);
  }
  
  //Tests the input to provide proper response to input submission
  /**
   * testInput
   * This method tests the input submitted by the player to see
   * if there is actually something to submit input for.
   */
  public int testInput(){
	  Item[] contentsArray = (thePlayer.getLoc()).getRoomContents();
	  if(contentsArray.length > 0){
		  return 1;
	  }
	  //else if(thePlayer.showMyThings() != ""){
		  //return 2;
	  //}
	  else
		  return 0;
  }
  
  /**
   * Get Player Health
   */
  public int getPlayerHealth(){
	  return thePlayer.getHealth();
  }
  
  /**
   * Get Weapon Strength
   */
  public int getWeaponStrength(){
	  Weapons w = (Weapons)thePlayer.getWeapon();
	  return w.getStrength();
  }
  
  //Warrior Stuff
  /**
   * Warrior Description
   */
  public String getWarriorDesc(){
	  if(thePlayer.getLoc().isWarrior()){
		  return thePlayer.getLoc().getWarrior().getName();
	  }
	  
	  return "blank_opp";
  }
  /**
   * Check if there is a warrior in the room
   */
  public boolean isWarrior(){
	  return thePlayer.getLoc().isWarrior();
  }
  
  /**
   * Get Warrior Health
   */
  public int getWarriorHealth(){
	  return thePlayer.getLoc().getWarrior().getHealth();
  }
  public void setWarriorHealth(int i){
	  thePlayer.getLoc().getWarrior().setHealth(i);
  }
  
  /**
   * ATTACK!!!!!
   */
  public String attackWarrior(){
	  Weapons temp = (Weapons)thePlayer.getWeapon();
	  int power = temp.getStrength();
	  int attackPower = (int)(Math.floor(Math.random() * power));
	  int warriorHealth = thePlayer.getLoc().getWarrior().getHealth();
	  int health = warriorHealth - attackPower;
	  thePlayer.getLoc().getWarrior().setHealth(health);
	  String str = "You attacked the oppenent with " + thePlayer.getWeaponDesc() + " and you damaged him " + attackPower + " points!";
	  if(thePlayer.getLoc().getWarrior().getHealth() > 0)
	  {
		  int warriorAttack = (int)(Math.floor(Math.random() * 30));
		  int playerHealth = thePlayer.getHealth() - warriorAttack;
		  thePlayer.setHealth(playerHealth);
		  str = str + "  Your opponent attacked you and damaged you " + warriorAttack + " points!"; 
	  }
	  else
	  {
		  str = str + "  Your opponent is dead, you're a murderer!";
	  }
	  return str;
  }
  public String warriorAttack(){
	  int warriorAttack = (int)(Math.floor(Math.random() * 30));
	  int playerHealth = thePlayer.getHealth() - warriorAttack;
	  thePlayer.setHealth(playerHealth);
	  String str = "You cant escape and now you are in trouble!" + "  Your opponent attacked you and damaged you " + warriorAttack + " points!";
	  
	  return str;
  }
  
  public void setPlayerInfo(Vector bob){
	  thePlayer.setHealth(Integer.parseInt((String)bob.get(4)));
	  Weapons w = new Weapons();
	  w.setDesc((String)bob.get(2));
	  w.setStrength(Integer.parseInt((String)bob.get(3)));
	  thePlayer.setWeapon(w);
	  for(int i = 6; i < bob.size(); i++)
	  {
		  Item temp = new Item();
		  temp.setDesc((String)bob.get(i));
		  thePlayer.addItem(temp);
	  }
	  thePlayer.setLoc(theCave.getRoomLoad(Integer.parseInt((String)bob.get(1))));
  }
  public void loadSavedRooms(Vector a)
  {
	  theCave.loadRoomsSaved(a);
  }
  
 
}
