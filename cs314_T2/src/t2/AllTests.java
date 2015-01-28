package t2;

/*****************************************************************************************
 * The Daemon Koders
 * March 5, 2008
 * Team Assignment 2
 * Class Testing the Cave Game
 *****************************************************************************************
 */

/*****************************************************************************************
 * AllTests.java is a test suite that runs all test classes at once.
 */

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for t2");
		//$JUnit-BEGIN$
		suite.addTestSuite(RoomTest.class);
		suite.addTestSuite(PlayerTest.class);
		suite.addTestSuite(DoorTest.class);
		//$JUnit-END$
		return suite;
	}
	
}
