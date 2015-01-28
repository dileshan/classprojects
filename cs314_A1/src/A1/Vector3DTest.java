
package A1;

import junit.framework.TestCase;

/**
 * @author Matt DeSilvey
 * @Date 1/28/08
 * @class cs314 
 * @assignment number: a1
 * Date due: 1/30/08 - 11:55pm
 *
 */
public class Vector3DTest extends TestCase {

	/**
	 * @param name
	 * Junit test constructor
	 */
	public Vector3DTest(String name) {
		super(name);
	}
	
	//Create vectors
	private Vector3D vect1;
	private Vector3D vect2;
	private Vector3D vect3;
	
	//second vector coordinates
	private double v2x = 3;
	private double v2y = 10;
	private double v2z = 3;
	
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 * Set up method to create tested objects on Vector3D class
	 */
	public void setUp() 
	{
		vect1 = new Vector3D(4,1,1);
		vect2 = new Vector3D(v2x,v2y,v2z);
		vect3 = new Vector3D(4,1,1);
	}
	
	public void testScale()
	{
		Vector3D tempVector = vect1.scale(3);
		
		assertEquals(tempVector, new Vector3D(12, 3, 3));
	}
	
	
	/**
	 * This method tests whether or not the objects tested are the same
	 */
	public void testVectorEquals()
	{
		//assertEquals(vect1, vect2);//should return false
		assertEquals(vect1, vect3);
		assertEquals(vect2, new Vector3D(v2x, v2y, v2z));
	}
	
	/**
	 * Test the add method within the Vector3D class
	 */
	public void testAddVector()
	{
		//Test add vector
		Vector3D tmpVect = vect1.add(vect2);
		Vector3D Vect = new Vector3D(7,11,4);//expected answer
		
		assertEquals(tmpVect, Vect);		
	}
	
	/**
	 * Tests the method substract with two vector objects 
	 * programmer create an expected answer.
	 */
	public void testSubVector()
	{
		//test subtract method
		Vector3D tmpVect = vect1.subtract(vect3);		
		Vector3D Vect = new Vector3D(0,0,0);//expected answer
		
		assertEquals(tmpVect, Vect);
		//assertEquals(tmpVect, new Vector3D(1,1,1));//should fail
	}
	
	/**
	 * Tests the negate method in the vector3d class
	 */
	public void testNegateVector()
	{
		Vector3D tmpVect = vect1.negate();
		
		assertEquals(tmpVect, new Vector3D(3,0,0));
		// **Fails** assertEquals(tmpVect, new Vector3D(5,2,2));
	}
	
	public void testMagVector()
	{
		double tmpVect = vect2.magnitude();
		double ans = 10.862780491200215;
		
		assertEquals(tmpVect,ans);
	}
	
	public void testDotVector()
	{
		double tempVect = vect1.dot(vect2);
		double expected = 25;
		
		assertEquals(tempVect, expected);
	}
	
	public void testEquals()
	{
		assertTrue(vect1.equals(vect3));
		// ** Fails ** assertTrue(vect1.equals(vect2));
	}
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
