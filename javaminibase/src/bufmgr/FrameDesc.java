/*
* Name: Matt DeSilvey
*
*/

package bufmgr;

import global.GlobalConst;
import global.PageId;
import diskmgr.*;

public class FrameDesc implements GlobalConst
{

	private int page_num;
	private int pin_count;
	private boolean dirtybit;
	private boolean empty = true;
	
	public FrameDesc ()
	{
		page_num = 0;//Tried using INVALID_PAGE but that was throwing an exception
		pin_count = 0;
		dirtybit = false;
	}
	
	public FrameDesc ( int page_num, int pin_count, boolean dirtybit )
	{
		setPageNum( page_num );
		this.pin_count = pin_count;
		this.dirtybit = dirtybit;
	}
	
	public int getPageNum() {
		return page_num;
	}

	public void setPageNum( int page_num ) {
		this.page_num = page_num;
		empty = false;
	}

	public int getPinCount() {
		return pin_count;
	}

	public void setPinCount( int pin_count ) {
		this.pin_count = pin_count;
	}

	public boolean isDirtyBit() {
		return dirtybit;
	}

	public void setDirtyBit( boolean dirtybit ) {
		this.dirtybit = dirtybit;
	}
	
	public void incrementPinCount(){
		pin_count++;
	}
	
	public void decrementPinCount(){
		pin_count--;
	}
	
	public boolean isEmpty()
	{
		return empty;
	}
	
	
	
}
