package t2;

/*****************************************************************************************
 * The Daemon Koders
 * March 5, 2008
 * Team Assignment 2
 * Class Testing the Cave Game
 *****************************************************************************************
 */

import static org.junit.Assert.*;

import java.io.IOException;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;


/*******************************************************************************************
 * RoomTest.java tests code within the Room.java class of the main program, some methods that aren't
 * within the Room might also be tested because the methods have concurrent implementation.
 *******************************************************************************************
 */
public class RoomTest extends TestCase
{
	Room testRoom, nextRoom;
	Item itemNew = new Item();
	Item nullItem = null;
	Player bob;
	
	/**************************************************************
	 * method setUp, begins the testing of objects within the program
	 */
	@Before
	public void setUp() throws Exception
	{
		testRoom = new Room();
		nextRoom = new Room();
		testRoom.setSide(2, nextRoom);
		nextRoom.setSide(3, testRoom);
		bob = new Player();
		itemNew.setDesc("Hand Grenade");
	}

	/***********************
	 * testAddItem only one test is done to add an item to a room
	 */
	@Test
	public void testAddItem1()
	{
		testRoom.addItem(itemNew);
		Item[] arrTest;
		arrTest = testRoom.getRoomContents().clone();
		assertTrue("Did not add the item correctly.", arrTest[0].getDesc().equals(itemNew.getDesc()));
	}

	/*********************************
	 * Test if you can remove an Item from a room, inserts two objects into a room so that the array
	 * doesn't have an out of bounds exception
	 */
	@Test
	public void testRemoveItem()throws IOException
	{
		Item gun = new Item();
		gun.setDesc("Ak-47");
		//Add two items so that the array is not null(and that there is no exception)
		testRoom.addItem(itemNew);
		testRoom.addItem(gun);
		//Test removeItem from room
		testRoom.removeItem(itemNew);
		Item[] arrTest;//testing array
		arrTest = testRoom.getRoomContents().clone();
		try{
			assertTrue("Method error", arrTest[0].getDesc().equals(gun.getDesc()));
		}catch(Exception e)
		{
			System.out.println("Method testRemoveItem(): -> Nothing in array can't remove" + e);
		}
	}

	/****************************************************
	 *  Test if you can enter a room.
	 *  
	 */
	@Test
	public void testEnter()
	{
		bob.setRoom(testRoom);
		nextRoom.enter(bob);
		assertTrue("Didn't enter the room.", bob.getLoc().equals(nextRoom));
	}
	
	/***************************************************************************************
	 * testExit1 and 2 test that if you can exit a specific room in a certain direction
	 */
	@Test
	public void testExit1() 
	{
		bob.setRoom(testRoom);
		testRoom.exit(2, bob);
		//The following line is the same as above except it checks to see if the player exited the room, but not
		//if the player successfully entered the next room.  
		assertTrue("Didn't enter the room.", !bob.getLoc().equals(testRoom));
	}
	
	@Test
	public void testExit2()
	{
		bob.setRoom(testRoom);
		testRoom.exit(1, bob);
		assertTrue("The player exited through a wall.", bob.getLoc().equals(testRoom));
		System.out.println("Player did not exit the room because he hit a wall.  ");
	}

}
