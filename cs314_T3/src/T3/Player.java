package a2;

import java.util.ArrayList;


/**  Cave Game  Program Code
     Copyright (c) 1999 James M. Bieman

     To compile: javac CaveGame.java
     To run:     java CaveGame

     The main routine is CaveGame.main
				    
**/


public class Player {

  private Room myLoc;
  private int health = 100;
  private Item[] myThings = new Item[4];
  private Weapons myWeapon = new Weapons();
  private int itemCount = 0;

  public void setWeapon(Weapons w){
	  myWeapon = w;
  }
  
  public Item getWeapon(){
	  return myWeapon;
  }
  
  public String getWeaponDesc(){
	  return myWeapon.getDesc();
  }
	  
  public void setRoom(Room r){
   myLoc = r;
   }

  public void setHealth(int i)
  {
	  health = i;
  }
  
  public int getHealth()
  {
	  return this.health;
  }
  
  public String look() {
   return myLoc.getDesc();
   }

  /**
   * This method has been modified to return the direction
   * of the place where the user is going.
   * 
   * @param direction
   * @return
   */
  public int go(int direction){
     int status = myLoc.exit(direction,this);
     return status;
  }

	public void pickUp(Item i)
	{
		if(i.getDesc().equalsIgnoreCase(null))
		{
			return;
		}
		else if (haveItem(i) == false && itemCount < 4)
		{
			myThings[itemCount] = i;
			itemCount++;
			myLoc.removeItem(i);
		}
	}
	
	public void addItem(Item i)
	{
		if(i.getDesc().equalsIgnoreCase(null))
		{
			return;
		}
		else if (haveItem(i) == false && itemCount < 4)
		{
			myThings[itemCount] = i;
			itemCount++;
			
		}
	}

  public boolean haveItem(Item itemToFind){
     for (int n = 0; n < itemCount ; n++){
    	// System.out.println(myThings[n].getDesc());
       if (myThings[n].getDesc().compareTo(itemToFind.getDesc()) == 0) return true;
       
     }
     return false;
  }

  public void drop(int itemNum){
   if (itemNum > 0 & itemNum <= itemCount){
      switch(itemNum){
      case 1: { myLoc.addItem(myThings[0]);
	        myThings[0]=myThings[1];
	        myThings[1]=myThings[2];
	        myThings[2]=myThings[3];
	        itemCount--; 
	        break;
	      }
      case 2: { myLoc.addItem(myThings[1]);
			myThings[1]=myThings[2];
	        myThings[2]=myThings[3];
		itemCount--;
		break;
	      }
      case 3: { myLoc.addItem(myThings[2]);
	        myThings[2]=myThings[3];
      	itemCount--;
      	break;
      	}
      case 4: { myLoc.addItem(myThings[3]);
      	itemCount--;
      	break;
      	}
      }
   }
   }

  public void setLoc(Room r){myLoc = r;}

  public Room getLoc(){return myLoc;}

  public String[] showMyThings2(){
	   String items[] = null;
	   for (int n = 0; n < itemCount ; n++)
	     items[n] = myThings[n].getDesc();
	   return items;
	  }
  
  public ArrayList showMyThings(){
		ArrayList items = new ArrayList();
		for (int n = 0; n < itemCount; n++) {
			items.add(myThings[n].getDesc());
		}
		return items;  
	}

  public boolean handsFull(){return itemCount==4;}

  public boolean handsEmpty(){return itemCount==0;}

  public int numItemsCarried(){return itemCount;}
  
  public int getPic()
  {
	  int picNum = 0;
	  if(this.getHealth() == health)
	  {
		  picNum = 1;
	  }
	  else if(this.getHealth() < health)
	  {
		  picNum = 2;
	  }
	  else if(this.getHealth() == 0)
	  {
		  picNum = 3;
	  }
	  return picNum;
  }

}

