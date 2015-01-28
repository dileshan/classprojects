/*
* Name: Matt DeSilvey
* 
*/

package bufmgr;

import java.util.ArrayList;
import java.util.Vector;

import diskmgr.*;
import global.*;

public class BufMgr implements GlobalConst
{
	
private int poolSize;
private byte bufPool[][];
private FrameDesc frameDescr[];
private HashTable directory;
private Clock replace;
	
	/**
	 * Create the BufMgr object.
	 * Allocate pages (frames) for the buffer pool in main memory and
	 * make the buffer manager aware that the replacement policy is
	 * specified by replacerArg (i.e. Clock, LRU, MRU etc.).
	 *
	 * @param numbufs: number of buffers in the buffer pool.
	 * @param replacerArg: name of the buffer replacement policy.
	 * @throws ErrorException 
	 */
	 public BufMgr( int numbufs, String replacerArg ) throws ErrorException 
	 {
		 initialize( numbufs, replacerArg );
	 }
	 
	 public void initialize( int numbufs, String replacerArg ) throws ErrorException
	 {
		//error check args
		 if( numbufs < 0 )
			 throw new ErrorException( null, "Error with numbufs must be greater than 0" );
		 if( !replacerArg.equals("Clock") )
			 throw new ErrorException( null, "Error with replacer policy" );
		 
		 poolSize = numbufs;
		 //init buffer pool
		 bufPool = new byte[poolSize][ MINIBASE_PAGESIZE ];
		 //init frames
		 frameDescr = new FrameDesc[poolSize];
		 
		 //Create the directory of hashes with page number, frame pairs
		 directory = new HashTable();
		 
		 //for each numbufs set a frameDesc object
		 for( int i = 0; i < poolSize; i++ )
			 frameDescr[i] = new FrameDesc();

		 //set up clocks replacement policy
		 replace = new Clock();
		 for( int i = 0; i < poolSize; i++ )
			 replace.add( new Integer(i) );
	 }
	  /**----------------------------------------------------------*/
	    /**
	     * Pin a page.
	     * First check if this page is already in the buffer pool.
	     * If it is, increment the pin_count and return a pointer to this
	     * page. If the pin_count was 0 before the call, the page was a
	     * replacement candidate, but is no longer a candidate.
	     * If the page is not in the pool, choose a frame (from the
	     * set of replacement candidates) to hold this page, read the
	     * page (using the appropriate method from {\em diskmgr} package) and pin it.
	     * Also, you must write out the old page in chosen frame if it is dirty
	     * before reading new page. (You can assume that emptyPage==false for
	     * this assignment.)
	     *
	     * @param pageId: page number in the minibase.
	     * @param page: the pointer point to the page.
	     * @param emptyPage: true (empty page); false (non-empty page)
	     * @throws Exception 
	     */
	 public void pinPage( PageId pin_pgid, Page page, boolean emptyPage ) throws Exception, BufferPoolExceededException
	 {
		 //check if page is in bufPool
		 int frameNo = directory.lookup( pin_pgid );
		 
		 /**
		  * If the page is already in the pool and pinned than just increment the pin count after
		  * checking the replacement policy of the current frame number
		  */
		 if( frameNo >= 0 )
		 {
			 //Make sure that its not in the queue
			 if( ( frameDescr[frameNo].getPinCount() == 0 ) )
				 replace.remove( frameNo );//replacement candidate
			 
			 //Increment the pin count
			 frameDescr[frameNo].incrementPinCount();
			 //set the page
			 page.setpage(bufPool[frameNo]);
			 
		 } else {
			 
			 //replacement stuff
			 frameNo = replace.getFrameFromQueue();
			 
			 //Must be in frame descr
			 if( frameNo < 0 )
				 throw new BufferPoolExceededException( null, "Error with replacment queue: requested frame not in queue" );
			 
			 //Check the frame is 
			 PageId olPgId = new PageId();
			 FrameDesc olFrDes = frameDescr[ frameNo ];
			 olPgId.pid = olFrDes.getPageNum();
			 
			 //Flush the page if the dirty bit is turned on
			 if( frameDescr[frameNo].isDirtyBit() )
				 flushPage( olPgId );
			 
			 //read the page
			 Page newPage = new Page();
			 if( !emptyPage )
			 {
				 try
				 {
					 SystemDefs.JavabaseDB.read_page( pin_pgid, newPage );
				 }
				 catch( Exception e )
				 {
					 throw e;
				 }
			 }
			 
			 //remove old page
			 if( !olFrDes.isEmpty() )
				 directory.remove( olPgId );
			 
			 //pin new page
			 frameDescr[frameNo] = new FrameDesc( pin_pgid.pid, 1, false );
			 
			 //copy frame into frameDescr and add the page into the bufferpool
			 FrameDesc fdes = new FrameDesc( pin_pgid.pid, 0, false );
			 frameDescr[frameNo] = fdes;
			  
			 //add the data to the bufferpool
			 byte[] tmpData = page.getpage();
			 for( int i = 0; i < MINIBASE_PAGESIZE; i++ )
			 {
				 bufPool[frameNo][i] = tmpData[i];
			 }
			 
			 //increment the pin count because its being used
			 frameDescr[frameNo].incrementPinCount();
			 //add the frame to the hash table
			 directory.add( pin_pgid, frameNo );
			 //set the buffer pool at the page
			 page.setpage( bufPool[frameNo] );
		 }
	 }
	  /**----------------------------------------------------------*/

	  
	  /**
	   * Unpin a page specified by a pageId.
	   * This method should be called with dirty==true if the client has
	   * modified the page. If so, this call should set the dirty bit
	   * for this frame. Further, if pin_count>0, this method should
	   * decrement it. If pin_count=0 before this call, throw an exception
	   * to report error. (For testing purposes, we ask you to throw
	   * an exception named PageUnpinnedException in case of error.)
	                                           2
	     *
	     * @param pageId: page number in the minibase.
	     * @param dirty the dirty bit of the frame
	     */
	 
	 public void unpinPage( PageId pageId, boolean dirty ) throws PageUnpinnedException, HashEntryNotFoundException
	 {
		 int frameNo = directory.lookup( pageId );
		 
		 if( frameNo >= 0 )
		 {
			 //if the frame is not in the frame array then you can't unpin it can you
			if( frameDescr[frameNo].getPinCount() == 0 )
				throw new PageUnpinnedException( null, "Page pin unpin error: " );
			
			//Set the dirty bit
			if( dirty )
				frameDescr[frameNo].setDirtyBit( dirty );
				
			//decrement the pin count
			frameDescr[frameNo].decrementPinCount();
			
			//if the pin count is zero than add it to the relacement policy
			if( frameDescr[frameNo].getPinCount() == 0 )
				replace.add( frameNo );
			
		 } else {
			throw new HashEntryNotFoundException( null, "No page in cache" );
		 }		 
	 }
	  /**----------------------------------------------------------*/

	    /**
	     * Allocate new pages.
	     * Call DB object to allocate a set of new pages and
	     * find a frame in the buffer pool for the first page
	     * and pin it. (This call allows a client of the Buffer Manager
	     * to allocate pages on disk.) If buffer is full, i.e., you
	     * can’t find a frame for the first page, ask DB to deallocate
	     * all these pages, and return null.
	     *
	     * @param firstpage the address of the first page.
	     * @param howmany total number of allocated new pages.
	     *
	     * @return the first page id of the new pages. null, if error.
	     */
	 
	 public PageId newPage( Page firstpage, int howmany ) throws PageAllocationException
	 {
		 PageId pgid = new PageId();
		 Page newPage = new Page();
		 
		 //Firstly try to allocate a set of new pages and than pin the new page
		 try
		 {
			SystemDefs.JavabaseDB.allocate_page( pgid, howmany );
			//Pin new page
			pinPage( pgid, newPage, true );
		 } catch ( Exception e )
		 {
			//Case where buffer is full so need to deallocate the 
			try{
				SystemDefs.JavabaseDB.deallocate_page( pgid, howmany );
			} catch( Exception dbEx )
			{
				throw new PageAllocationException( dbEx, "Error with deallocate page" );
			}
			pgid = null;
		 }
		 
		 //set new page
		 //newPage.setpage( firstpage.getpage() );
		 firstpage.setpage( newPage.getpage() );
		 return pgid;
	 }
	  /**----------------------------------------------------------*/

	  /**
	   * This method should be called to delete a page that is on disk.
	   * This routine must call the method in diskmgr package to
	   * deallocate the page.
	   *
	   * @param globalPageId the page number in the data base.
	 * @throws PageUnpinnedException 
	   */
	  public void freePage( PageId globalPageId ) throws PageAllocationException,
								 PagePinnedException,
								 HashEntryNotFoundException, PageUnpinnedException
	  {
		 //Get frameNo from hash table
		 int frameNo = directory.lookup( globalPageId );
		 int pinCount = frameDescr[frameNo].getPinCount();
		 
		 //if the frame is in the pool and pinned than error out because you can't free a page that is being used
		 if( ( pinCount > 1 ) && ( frameNo == -1 ) )
			throw new PagePinnedException( null, "Unable free the current page pin count is greater than ." );
		 
		 //If the pin count is at least one than unpin the page
		 if( pinCount == 1 )
			unpinPage( globalPageId, false );
		 
		 try
		 {
			//Deallocate page from database
			SystemDefs.JavabaseDB.deallocate_page( globalPageId );
		 } catch ( Exception e )
		 {
			throw new PageAllocationException( e, "Pinned Failed, Error with freeing page" );
		 }
		 
		//unpin page and remove hash value
		 frameDescr[ directory.lookup( globalPageId ) ] = new FrameDesc();
		 
		//remove from clock
		if( replace.find(frameNo) )
			replace.remove( frameNo );
			
		//remove frame from hashtable
		directory.remove( globalPageId );
	  }
	/**----------------------------------------------------------*/
	   
	  /**
	   * Used to flush a particular page of the buffer pool to disk.
	   * This method calls the write_page method of the diskmgr package.
	   *
	   * @param pageid the page number in the database.
	   */
	                                           
	  public void flushPage( PageId pageid ) throws HashEntryNotFoundException, ErrorException
	  {
		 int frameNo = directory.lookup( pageid );
		 
		 if( frameNo != -1 )
		 {
			try
			{
				//Write the page to the DB
				SystemDefs.JavabaseDB.write_page( pageid, new Page( bufPool[frameNo] ) );
				
				//Reset the dirty bit to false
				frameDescr[frameNo].setDirtyBit( false );
			} catch ( Exception e )
			{
				throw new ErrorException( e, "Write Failed" );
			}
		 } else {
			//Page not in pool
			throw new HashEntryNotFoundException( null, "Page not in pool" );
		 }
      }
	  /**----------------------------------------------------------
	  */
	  
	  /**
	   * Flush all pages from the hashtable
	   * @param void
	   * @throws ErrorException
	   */
	  
	  public void flushAllPages() throws ErrorException
	  {
			ArrayList<Bucket> buckets;
			//Fill vector with all the buckets from the directory
			buckets = directory.getAllBuckets();
			
			for( Bucket elem : buckets )
			{
				try
				{PageId pgid = new PageId();
					pgid.pid = elem.getPageNumber();
					flushPage(pgid);
				} catch ( HashEntryNotFoundException e )
				{
					throw new ErrorException( null, "This should never throw" );
				}
			}
	  }
	  
	  /** Gets the total number of unpinned buffer frames.
	  *
	  * @return total number of unpinned buffer frames.
	  */
	  
	  public int getNumUnpinnedBuffers(){
		  //if there is anything in the replacement than those frames are unpinned
		  return replace.size();
	  }
	  
	private int getNumBuffers() {
		return poolSize;
	}
	
	/**
	 * 
	 * @author mattaiss
	 *
	 * Sub Class for Replacement policy 
	 */
	class Clock
	{
		/**
		 * Simple vector for storing the unpinned pages
		 */
		private Vector<Integer> framequeue;
		
		Clock()
		{
			framequeue = new Vector<Integer>();
		}
		
		/**
		 * Simple Add, add the new frame to the end of the list
		 * @param frame
		 */
		public void add( int frame )
		{
			framequeue.add( new Integer(frame) );
		}
		
		/**
		 * 
		 * @param frame
		 * @return
		 */
		public boolean remove( int frame )
		{
			if( framequeue.isEmpty() )
				return false;
			
			for( Integer elem : framequeue ) {
				if( elem.intValue() == frame ){
					int index = framequeue.indexOf( elem.intValue() );
					framequeue.remove(index);
					break;
				}
			}
			return true;
		}
		
		/**
		 * Always gets the frame from the beginning of the list like a queue
		 * @return
		 */
		public int getFrameFromQueue( )
		{
			if( framequeue.isEmpty()  )
				return -1;
				
			int frame = (framequeue.firstElement()).intValue();
			framequeue.remove( 0 );//remove the first element of the queue
			return frame;
		}
		
		/**
		 * Used to see if the page is unpined or not
		 * @param frame
		 * @return
		 */
		public boolean find( int frame )
		{
			for( Integer elem : framequeue )
			{
				if( elem.intValue() == frame )
					return true;
			}
			return false;
		}
		
		/**
		 * return the number of unpinned frames
		 * @return
		 */
		public int size()
		{
			return framequeue.size();
		}
		
		public boolean empty()
		{
			return framequeue.isEmpty();
		}
	}

}
