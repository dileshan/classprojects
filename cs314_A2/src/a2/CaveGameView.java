package a2;

import java.awt.*;
import java.io.IOException;

import BreezyGUI.*;

public class CaveGameView extends GBFrame{

   // Window objects --------------------------------------
   Label welcomeLabel =
     addLabel("Welcome to the Cave Game, " + 
              "which is inspired by an old game called adventure." +
            " Java implementation Copyright (c) 1999-2002 by James M. Bieman",
	    1,1,5,1);
   
   Label viewLable = addLabel ("Your View: ",2,1,1,1);
   TextArea viewArea = addTextArea("Start",3,1,4,3); 

   Label carryingLable = addLabel ("You are carying: ",6,1,1,1);
   TextArea carryingArea = addTextArea("Nothing",7,1,4,3); 

Label separator1 = addLabel
   ("-----------------------------------------------------------------"
	 , 10,1,4,1);


   Label choiceLabel    = addLabel
      ("Choose a direction, pick-up, or drop an item" ,11,1,5,1);

   Button grabButton = addButton ("Grab an item", 12, 5,1,1);
   Button dropButton = addButton ("Drop an item", 13, 5,1,1);
   
   Button northButton = addButton ("North", 12,2,1,1);
   Button southButton = addButton ("South", 14,2,1,1);
   Button eastButton = addButton ("East",   13,3,1,1);
   Button westButton = addButton ("West",   13,1,1,1);
   Button upButton = addButton ("Up", 12,3,1,1);
   Button downButton = addButton ("Down", 14,3,1,1);

   CaveGameModel model;
   
   // Constructor-----------------------------------------------

   public CaveGameView() throws IOException{
      setTitle ("Cave Game");
      model = new CaveGameModel();

      viewArea.setEditable (false);
      carryingArea.setEditable (false);
      displayCurrentInfo();
   } 
   
   
   // buttonClicked method--------------------------------------

   public void buttonClicked (Button buttonObj){
      if (buttonObj == upButton)
         model.goUp();

      else if (buttonObj == downButton)
	 model.goDown();

      else if (buttonObj == northButton)
	 model.goNorth();

      else if (buttonObj == southButton)
	 model.goSouth();

      else if (buttonObj == eastButton)
	 model.goEast();

      else if (buttonObj == westButton)
	 model.goWest();

      else if (buttonObj == grabButton)
	 grab();

      else if (buttonObj == dropButton)
	 drop();

      displayCurrentInfo();
  }
      
   
   // Private methods-------------------------------------------

   private void displayCurrentInfo(){
	 viewArea.setText(model.getView());
	 carryingArea.setText(model.getItems());
	 }

    // Left as an exercise. 
   private void grab() {
      //  Set up a dialog to talk to the model and
      //  determine what items to pick up.
	  if(model.grabCheckFull() == 1)
	  {
		  carryingArea.setText("Can't grab anymore items, inventory is full.");
	  }
	  if(model.grabCheckFull() == 0)
	  {
		  carryingArea.setText("Can't grab anything, no items there.");
	  }
	  if(model.grabCheckFull() == -1)
	  {
		  carryingArea.setText(model.graby());
	  }  
	 
   }

    // Left as an exercise. 
    private void drop() {
	     //  Set up a dialog to talk to the model and 
         //  determine what items to pick up.
    	if(model.dropCheck() == -1)
    	{
    		carryingArea.setText("You have nothing to drop");
    	}
    	else
    	{
    		carryingArea.setText(model.drop());
    	}
   }

   public static void main (String[] args) throws IOException{
      Frame view = new CaveGameView();
      view.setSize (800, 600); /* was 400, 250  */             
      view.setVisible(true);    
   }                    
}
