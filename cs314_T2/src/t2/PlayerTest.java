package t2;

/*****************************************************************************************
 * The Daemon Koders
 * March 5, 2008
 * Team Assignment 2
 * Class Testing the Cave Game
 *****************************************************************************************
 */

import static org.junit.Assert.*;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/*******************************************************************************************
 * PlayerTest.java tests the code that is player of the main program, some methods that aren't
 * within the player might also be tested because the methods have concurrent implementation.
 *******************************************************************************************
 */
public class PlayerTest extends TestCase
{
	Room testRoom, nextRoom;
	Item testItem, testItem2;
	Player Rooney;
	
	/************************************************************************
	 * Set certain objects for testing
	 */
	@Before
	public void setUp() throws Exception
	{
		testItem = new Item();
		testItem2 = new Item();
		testRoom = new Room();
		nextRoom = new Room();
		testRoom.setSide(2, nextRoom);
		nextRoom.setSide(3, testRoom);
		Rooney = new Player();
		testItem.setDesc("Ball");
		testItem.setDesc("Cleats");
		Rooney.setLoc(testRoom);
	}

	
	/**************************************************************************************************
	 * Test the go which uses the enter in the room class
	 */
	@Test
	public void testGo()
	{
		Rooney.go(2);
		assertTrue("The player did not go to the next room correctly.", Rooney.getLoc().equals(nextRoom));
	}
	
	
	//Test go for another direction 
	@Test
	public void testGo1()
	{
		Rooney.go(3);
		assertTrue("No dice", !Rooney.getLoc().equals(nextRoom));
	}

	/************************************************************************************************
	 * Test if the player can pickup items within the room.
	 */
	@Test
	public void testPickUp1()
	{
		testRoom.addItem(testItem);
		Rooney.pickUp(testItem);
		assertTrue("The player didn't pick up the item.", Rooney.haveItem(testItem));
	}
	//Pickup two items
	@Test
	public void testPickUp2()
	{
		testRoom.addItem(testItem);
		testRoom.addItem(testItem2);
		Rooney.pickUp(testItem);
		Rooney.pickUp(testItem2);
		assertTrue("The player didn't pick up the second item", Rooney.numItemsCarried() == 2);
	}

	/*********************************************************************************
	 * test the drop method in the player class, to find out if the player can drop the 
	 * item correctly. Pick up one item
	 */
	@Test
	public void testDrop1()
	{
		testRoom.addItem(testItem);
		Rooney.pickUp(testItem);
		Rooney.drop(1);
		assertTrue("The player didn't drop the item.", !Rooney.haveItem(testItem));
	}
	
	//Pick up two Items
	@Test
	public void testDrop2()
	{
		testRoom.addItem(testItem);
		testRoom.addItem(testItem2);
		Rooney.pickUp(testItem);
		Rooney.pickUp(testItem2);
		Rooney.drop(2);
		assertTrue("The player didn't drop the item.", !Rooney.haveItem(testItem2));
	}
	
	
	
}
