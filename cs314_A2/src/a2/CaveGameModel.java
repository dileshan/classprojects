package a2;

import java.io.IOException;

public class CaveGameModel{

 // some private fields to reference current location,
 // its description, what I'm carrying, etc.
 //
 // These methods and fields are left as exercises.
	
	CaveGame theGame;
	Player thePlayer;
	Cave theCave;
	Room startRm;

  CaveGameModel() throws IOException { // we initialize
		 theGame = new CaveGame();//create game
		 thePlayer = new Player();
		 theCave = new Cave();
		 startRm = theCave.createCave();
		 thePlayer.setRoom(startRm);
  }

  public void goUp(){
      thePlayer.go(4);
      thePlayer.getLoc();
  }

  public void goDown(){
	  thePlayer.go(5);
	  thePlayer.getLoc();
    }

  public void goNorth(){
	  thePlayer.go(0);
	  thePlayer.getLoc();
    }
      
  public void goSouth(){
	  thePlayer.go(1);
	  thePlayer.getLoc();
    }

  public void goEast(){
	  thePlayer.go(2);
	  thePlayer.getLoc();
    }
      
  public void goWest(){
	  thePlayer.go(3);
	  thePlayer.getLoc();
    }

  // You need to finish these getView and getItems methods.
  public String getView(){	  
	  return(thePlayer.look());
     }

  public String getItems()
  {
	 if(thePlayer.showMyThings() == "")
	 {
		 return "Player carrying nothing";
	 }
	 else{
		 return(thePlayer.showMyThings());
	 }
  }

 // Surely you will need other methods to deal with
 // picking up and dropping things.
  
  public int grabCheckFull(){
	  int full = 1;
	  int roomEmpty = 0;
	  int empty = -1;//if players inventory is empty than grab
	  if(thePlayer.handsFull())
	  {
		  return full;
	  }
	  if((thePlayer.getLoc()).roomEmpty())
	  {
		  return roomEmpty;
	  }
	  else{		  
		  return empty;
	  }
  }
  
  public String graby()
  {
	  Item itemToGrab = choosePickupItem(thePlayer); //pick
      thePlayer.pickUp(itemToGrab);
      (thePlayer.getLoc()).removeItem(itemToGrab);
	  return (thePlayer.showMyThings()) + "";
  }
  
  public int dropCheck()
  {
	  if(thePlayer.handsEmpty())
	  {
		  return -1;
	  }
	  else
	  {
		  return 0;
	  }
  }
  
  private Item choosePickupItem(Player p)
  {
	  Item[] contentsArray = (p.getLoc()).getRoomContents();
	  
	  for(int i = 0; i < contentsArray.length-1; i++)
	  {
		  System.out.println(contentsArray[i]);
	  }

	   return contentsArray[0];
  }
  
  public String drop()
  {
	  thePlayer.drop(1);
	  return "Item Dropped here";
  }

}
