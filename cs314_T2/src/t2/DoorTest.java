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
 * DoorTest.java tests code within the Door class of the main program, some methods that aren't
 * within the door might also be tested because the methods have concurrent implementation.
 *******************************************************************************************
 */

public class DoorTest extends TestCase
{
	Key theKey, notTheKey;
	Door testDoor;
	Room testRoom, nextRoom;
	Player dave;
	
	/****************************************
	 * Set up certain objects for testing within the class Door
	 */
	@Before
	public void setUp() throws Exception 
	{
		theKey = new Key();
		notTheKey = new Key();
		testRoom = new Room();
		nextRoom = new Room();
		testRoom.setSide(2, nextRoom);
		nextRoom.setSide(3, testRoom);
		testDoor = new Door(testRoom, nextRoom, theKey);
		dave = new Player();
	}

	/***************************************************************
	 * Three different test cases are used to test the enter method within
	 * class Door.
	 */
	@Test
	public void testEnter1()
	{
		dave.setRoom(testRoom);
		testRoom.addItem(theKey);
		dave.pickUp(theKey);
		testDoor.enter(dave);
		assertTrue("The key and the door work.", dave.getLoc().equals(nextRoom));
	}
	
	//testEnter2 tests that the player cannot pass through a door in the cave without possessing the proper key.  
	@Test
	public void testEnter2()
	{
		dave.setRoom(testRoom);
		testDoor.enter(dave);
		assertTrue("The player passed through the door without the key.", !dave.getLoc().equals(nextRoom));
	}
	
	//testEnter3 test that the player cannot pass through a door without the correct key. (A player can have a key
	//but if it doesn't match the door then they cannot get through)
	@Test
	public void testEnter3()
	{
		dave.setRoom(testRoom);
		testRoom.addItem(notTheKey);
		dave.pickUp(notTheKey);
		testDoor.enter(dave);
		assertTrue("The player passed through the door without the right key.", !dave.getLoc().equals(nextRoom));
	}

}
