package A1;
import java.lang.*;
import java.util.*;

/**
 * @author Matt DeSilvey
 * @Date 1/28/08
 * @class cs314 
 * @assignment number: a1
 * Date due: 1/30/08 - 11:55pm
 *
 */

/****************************************************************************************************
 * Class Vector3D description: Program class has does basic math arithmetic on vectors
 * class is tested on using junit test cases.
 ****************************************************************************************************
 */

public class Vector3D {

	private double x;
	private double y;
	private double z;
	
	//default constructor
	public Vector3D()
	{
		
	}
	
	public Vector3D(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/***********************************************************************************************
	 * @param f
	 * @Description : scale method takes in one parameter which is the factor and returns a new 
	 * vector object that is scaled.
	 ***********************************************************************************************
	 */	
	public Vector3D scale(double f)
	{		
		return new Vector3D(x*f,y*f,z*f);
	}
	
	/***********************************************************************************************
	 * @param v
	 * @return new vector object.
	 * @Description : adds two vectors together and returns a new vector object.
	 ************************************************************************************************
	 */	
	public Vector3D add(Vector3D v)
	{
		double x1Tmp;
		double y1Tmp;
		double z1Tmp;
		
		x1Tmp = v.x + x;
		y1Tmp = v.y + y;
		z1Tmp = v.z + z;
						
		return new Vector3D(x1Tmp, y1Tmp, z1Tmp);
	}
	
	/************************************************************************************************
	 * @param v
	 * @return new vector object with new parameters
	 * @Description : Same has add except that the method subtracts the two given vectors.
	 ************************************************************************************************
	 */
	public Vector3D subtract(Vector3D v)
	{
		double x1Tmp;
		double y1Tmp;
		double z1Tmp;
				
		x1Tmp = v.x - x;
		y1Tmp = v.y - y;
		z1Tmp = v.z - z;
				
		return new Vector3D(x1Tmp, y1Tmp, z1Tmp);
	}
	
	/************************************************************************************************
	 * @return
	 */
	public Vector3D negate()
	{
		return new Vector3D(this.x-1, this.y-1, this.z-1);
	}

	/************************************************************************************************
	 * 
	 * @returns a double value for the calculated vector value
	 * 
	 ************************************************************************************************
 	 */
	public double magnitude()
	{
		double mag;
		//Vectors magnitude 
		mag = Math.sqrt(x*x + y*y + z*z);		
		return  mag;
	}
	
	/************************************************************************************************
	 * 
	 * @param v
	 * @return
	 * 
	 * returns the dot product of the two vectors as a double value
	 ***********************************************************************************************
	 */
	public double dot(Vector3D v)
	{
		double dot = v.x*this.x + v.y*this.y + v.z*this.z;
		
		return dot;
	}
	
	/************************************************************************************************
	 * 
	 * @param v Vector3D object
	 * @returns a string value
	 * 
	 ************************************************************************************************
	 */
	public String toString()
	{
		return "Given vector: \n" + "x-axis: " + this.x + "\n"
		+ "y-axis: " + this.y + "\nz-axis: " + this.z;
	}
	
	/************************************************************************************************
	 * @param obj
	 * @return a boolean value
	 * 
	 * Equals method takes in a object parameter and converts it to a Vector3D object 
	 * If the two Vector3D objects compare method returns a boolean value true 
	 ************************************************************************************************ 
	 */
	public boolean equals(Object obj) 
	{
		Vector3D compareVector = (Vector3D)obj;
		
		if((compareVector.x == x) && (compareVector.y == y) 
				&& (compareVector.z == z))
		{
			return true;
		}
		else
			return false;
	}
}
