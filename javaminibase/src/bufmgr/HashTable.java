/*
* Name: Matt DeSilvey
*
*/

package bufmgr;

import java.util.*;

import global.*;
import diskmgr.*;

public class HashTable implements GlobalConst
{
	private final int HTSIZE = 23;
	
	private Map<Integer, Bucket> hash;
	
	private ArrayList<Bucket> directory;
	
	/**
	 * 
	 */
	public HashTable(  )
	{
		directory = new ArrayList<Bucket>( HTSIZE );
	}
	
	/**
	 * 
	 * @param pageNo
	 * @param frameNo
	 */
	
	public void add( PageId pageNo, int frameNo )
	{
		int key = hash( pageNo.pid );
		Bucket bucket = new Bucket( pageNo.pid, frameNo );
	
		directory.add( bucket );
	}
	
	/**
	 * 
	 * @param pageNo
	 * @return
	 */
	
	public boolean remove( PageId pageNo )
	{
		if( directory.isEmpty() )
			return false;
		
		int key = hash( pageNo.pid );
		Bucket bucket = null;
		
		bucket = directory.get( key );
		
		while( bucket != null )
			directory.remove( bucket );
		return true;
	}
	
	
	/**
	 * 
	 * @param pageNo
	 * @return
	 */
	public int lookup( PageId pageNo )
	{	
		if( directory.isEmpty() )
			return -1;
		
		int key = hash( pageNo.pid );
		Bucket bucket = null;
		
		//check next case where the next lookup is null
		//Also check that where in bounds for the get other wise will get an exception thrown
		if( key < 0 || key >= directory.size() )
			return -1;
		bucket = directory.get( key );
         
		int frameNo = bucket.getFrameNumber();
		if( frameNo >= 0 )
			return frameNo;
		
		return -1;
	}
	
	/**
	 * 
	 * @param pgid
	 * @return Bucket object
	 */
	
	public Bucket getBucket( PageId pgid )
	{
		if( directory.isEmpty() )
			return null;
		
		int key = hash( pgid.pid );
		Bucket bucket = directory.get( key );
		
		if( bucket != null )
			return bucket;
		else
			return null;
	}
	
	/**
	 * 
	 * @return ArrayList<Bucket>
	 */
	
	public ArrayList<Bucket> getAllBuckets()
	{
		if( directory.isEmpty() )
			return null;
		
		ArrayList<Bucket> allBuckets = new ArrayList<Bucket>();
		Bucket bucket;
		
		/*Getting an OutOfMemoryError*/
		for( Bucket elem : directory )
		{
			bucket = elem;
			if( bucket != null )
			{
				allBuckets.add( bucket );
			}
		}
		
		return allBuckets;
	}

	/**
	 * Custom hash function
	 * @param pid
	 * @return
	 */
	private int hash( int pid )
	{
		return( pid % HTSIZE );
	}
}
