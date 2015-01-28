/*
* Name: Matt DeSilvey
*
*/

package bufmgr;

import global.*;

public class Bucket implements GlobalConst
{

	private int pageNum;
	private int frameNum;
	private Bucket next;
	
	
	Bucket( int pageNo, int frameNo )
	{
		pageNum = pageNo;
		frameNum = frameNo;
	}
	
	public int getPageNumber()
	{
		return pageNum;
	}
	
	public int getFrameNumber()
	{
		return frameNum;
	}

	public void setPageNum( int pageNum ) {
		this.pageNum = pageNum;
	}

	public void setFrameNum( int frameNum ) {
		this.frameNum = frameNum;
	}
	
	public void setNextPair( Bucket nextPair )
	{
		next = nextPair;
	}
	
	public Bucket getNextPair()
	{
		return next;
	}
	
	public boolean equals( Object o )
	{
		if( ( o == null ) && !( o instanceof Bucket ) )
			return false;
		
		Bucket obj = (Bucket) o;
		
		if( this.getPageNumber() == obj.getPageNumber() &&
				this.getFrameNumber() == obj.getFrameNumber() &&
				this.getNextPair().equals( obj.getNextPair() ) )
			return true;
		else
			return false;
	}
}
