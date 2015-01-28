
/**
 * Matt DeSilvey
 * Homework 7
 */


package heap;

import java.io.*;

import javax.naming.directory.SearchControls;

import com.sun.org.apache.regexp.internal.RE;

import diskmgr.*;
import bufmgr.*;
import global.*;

/**  This heapfile implementation is directory-based. We maintain a
 *  directory of info about the data pages (which are of type HFPage
 *  when loaded into memory).  The directory itself is also composed
 *  of HFPages, with each record being of type DataPageInfo
 *  as defined below.
 *
 *  The first directory page is a header page for the entire database
 *  (it is the one to which our filename is mapped by the DB).
 *  All directory pages are in a doubly-linked list of pages, each
 *  directory entry points to a single data page, which contains
 *  the actual records.
 *
 *  The heapfile data pages are implemented as slotted pages, with
 *  the slots at the front and the records in the back, both growing
 *  into the free space in the middle of the page.
 *
 *  We can store roughly pagesize/sizeof(DataPageInfo) records per
 *  directory page; for any given HeapFile insertion, it is likely
 *  that at least one of those referenced data pages will have
 *  enough free space to satisfy the request.
 */


/** DataPageInfo class : the type of records stored on a directory page.
*
*/


interface  Filetype {
  int TEMP = 0;
  int ORDINARY = 1;
  
} // end of Filetype

public class Heapfile implements Filetype,  GlobalConst {
  
  
  PageId      _firstDirPageId;   // page number of header page
  int         _ftype;
  private     boolean     _file_deleted;
  private     String 	 _fileName = "";
  private static int tempfilecount = 0;
  private PageId hpId;
  
  
  
  
  
  
  /* get a new datapage from the buffer manager and initialize dpinfo
     @param dpinfop the information in the new HFPage
  */
  private HFPage _newDatapage(DataPageInfo dpinfop)
    throws HFException,
	   HFBufMgrException,
	   HFDiskMgrException,
	   IOException
    {
      Page apage = new Page();
      PageId pageId = new PageId();
      pageId = newPage(apage, 1);
      
      if(pageId == null)
	throw new HFException(null, "can't new paGe");
      
      // initialize internal values of the new page:
      
      HFPage hfpage = new HFPage();
      hfpage.init(pageId, apage);
      
      dpinfop.pageId.pid = pageId.pid;
      dpinfop.recct = 0;
      dpinfop.availspace = hfpage.available_space();
      
      return hfpage;
      
    } // end of _newDatapage
  
  /* Internal HeapFile function (used in getRecord and updateRecord):
     returns pinned directory page and pinned data page of the specified 
     user record(rid) and true if record is found.
     If the user record cannot be found, return false.
  */
  private boolean  _findDataPage( RID rid,
				  PageId dirPageId, HFPage dirpage,
				  PageId dataPageId, HFPage datapage,
				  RID rpDataPageRid) 
    throws InvalidSlotNumberException, 
	   InvalidTupleSizeException, 
	   HFException,
	   HFBufMgrException,
	   HFDiskMgrException,
	   Exception
    {
      PageId currentDirPageId = new PageId(_firstDirPageId.pid);
      
      HFPage currentDirPage = new HFPage();
      HFPage currentDataPage = new HFPage();
      RID currentDataPageRid = new RID();
      PageId nextDirPageId = new PageId();
      // datapageId is stored in dpinfo.pageId 
      
      
      pinPage(currentDirPageId, currentDirPage, false/*read disk*/);
      
      Tuple atuple = new Tuple();
      
      while (currentDirPageId.pid != INVALID_PAGE)
	{// Start While01
	  // ASSERTIONS:
	  //  currentDirPage, currentDirPageId valid and pinned and Locked.
	  
	  for( currentDataPageRid = currentDirPage.firstRecord();
	       currentDataPageRid != null;
	       currentDataPageRid = currentDirPage.nextRecord(currentDataPageRid))
	    {
	      try{
		atuple = currentDirPage.getRecord(currentDataPageRid);
	      }
	      catch (InvalidSlotNumberException e)// check error! return false(done) 
		{
		  return false;
		}
	      
	      DataPageInfo dpinfo = new DataPageInfo(atuple);
	      try{
		pinPage(dpinfo.pageId, currentDataPage, false/*Rddisk*/);
		
		
		//check error;need unpin currentDirPage
	      }catch (Exception e)
		{
		  unpinPage(currentDirPageId, false/*undirty*/);
		  dirpage = null;
		  datapage = null;
		  throw e;
		}
	      
	      
	      
	      // ASSERTIONS:
	      // - currentDataPage, currentDataPageRid, dpinfo valid
	      // - currentDataPage pinned
	      
	      if(dpinfo.pageId.pid==rid.pageNo.pid)
		{
		  atuple = currentDataPage.returnRecord(rid);
		  // found user's record on the current datapage which itself
		  // is indexed on the current dirpage.  Return both of these.
		  
		  dirpage.setpage(currentDirPage.getpage());
		  dirPageId.pid = currentDirPageId.pid;
		  
		  datapage.setpage(currentDataPage.getpage());
		  dataPageId.pid = dpinfo.pageId.pid;
		  
		  rpDataPageRid.pageNo.pid = currentDataPageRid.pageNo.pid;
		  rpDataPageRid.slotNo = currentDataPageRid.slotNo;
		  return true;
		}
	      else
		{
		  // user record not found on this datapage; unpin it
		  // and try the next one
		  unpinPage(dpinfo.pageId, false /*undirty*/);
		  
		}
	      
	    }
	  
	  // if we would have found the correct datapage on the current
	  // directory page we would have already returned.
	  // therefore:
	  // read in next directory page:
	  
	  nextDirPageId = currentDirPage.getNextPage();
	  try{
	    unpinPage(currentDirPageId, false /*undirty*/);
	  }
	  catch(Exception e) {
	    throw new HFException (e, "heapfile,_find,unpinpage failed");
	  }
	  
	  currentDirPageId.pid = nextDirPageId.pid;
	  if(currentDirPageId.pid != INVALID_PAGE)
	    {
	      pinPage(currentDirPageId, currentDirPage, false/*Rdisk*/);
	      if(currentDirPage == null)
		throw new HFException(null, "pinPage return null page");  
	    }
	  
	  
	} // end of While01
      // checked all dir pages and all data pages; user record not found:(
      
      dirPageId.pid = dataPageId.pid = INVALID_PAGE;
      
      return false;   
      
      
    } // end of _findDatapage		
  
  /*public Heapfile( String name ) throws Exception
  {
	  if (name == null)
      {
              name = "tryHeapFile";
      }
      _fileName = name;
     
      try
      {
              //See if file exists.
              _firstDirPageId = get_file_entry( _fileName );
             
              //If null, create new page.
              if ( _firstDirPageId == null)
              {
                      //make head page.
                      HFPage headPage = new HFPage();
                      _firstDirPageId = newPage( headPage, 1 );
                      headPage.setCurPage( _firstDirPageId );
                      headPage.setFreeSpace();
                      unpinPage( _firstDirPageId, true );
                     
                      //add entry
                      add_file_entry( _fileName, _firstDirPageId );
              }
      }
      catch ( Exception e )
      {
              throw e;
      }
  }*/
  
  /** Initialize.  A null name produces a temporary heapfile which will be
   * deleted by the destructor.  If the name already denotes a file, the
   * file is opened; otherwise, a new empty file is created.
   *
   * @exception HFException heapfile exception
   * @exception HFBufMgrException exception thrown from bufmgr layer
   * @exception HFDiskMgrException exception thrown from diskmgr layer
   * @exception IOException I/O errors
   */
  public  Heapfile(String name) 
    throws HFException, 
	   HFBufMgrException,
	   HFDiskMgrException,
	   IOException
	   
    {
      // Give us a prayer of destructing cleanly if construction fails.
      _file_deleted = true;
      _fileName = null;
      
      if(name == null) 
	{
	  // If the name is NULL, allocate a temporary name
	  // and no logging is required.
	  _fileName = "tempHeapFile";
	  String useId = new String("user.name");
	  String userAccName;
	  userAccName = System.getProperty(useId);
	  _fileName = _fileName + userAccName;
	  
	  String filenum = Integer.toString(tempfilecount);
	  _fileName = _fileName + filenum; 
	  _ftype = TEMP;
	  tempfilecount ++;
	  
	}
      else
	{
	  _fileName = name;
	  _ftype = ORDINARY;    
	}
      
      // The constructor gets run in two different cases.
      // In the first case, the file is new and the header page
      // must be initialized.  This case is detected via a failure
      // in the db->get_file_entry() call.  In the second case, the
      // file already exists and all that must be done is to fetch
      // the header page into the buffer pool
      
      // try to open the file
      
      Page apage = new Page();
      _firstDirPageId = null;
      if (_ftype == ORDINARY)
	_firstDirPageId = get_file_entry(_fileName);
      
      if(_firstDirPageId==null)
	{
	  // file doesn't exist. First create it.
	  _firstDirPageId = newPage(apage, 1);
	  // check error
	  if(_firstDirPageId == null)
	    throw new HFException(null, "can't new page");
	  
	  add_file_entry(_fileName, _firstDirPageId);
	  // check error(new exception: Could not add file entry
	  
	  HFPage firstDirPage = new HFPage();
	  firstDirPage.init(_firstDirPageId, apage);
	  PageId pageId = new PageId(INVALID_PAGE);
	  
	  firstDirPage.setNextPage(pageId);
	  firstDirPage.setPrevPage(pageId);
	  unpinPage(_firstDirPageId, true /*dirty*/ );
	  
	  
	}
      _file_deleted = false;
      // ASSERTIONS:
      // - ALL private data members of class Heapfile are valid:
      //
      //  - _firstDirPageId valid
      //  - _fileName valid
      //  - no datapage pinned yet    
      
    } // end of constructor 
    
  
  /** Return number of records in file.
   *
   * @exception InvalidSlotNumberException invalid slot number
   * @exception InvalidTupleSizeException invalid tuple size
   * @exception HFBufMgrException exception thrown from bufmgr layer
   * @exception HFDiskMgrException exception thrown from diskmgr layer
   * @exception IOException I/O errors
   */
  public int getRecCnt() throws InvalidSlotNumberException, 
  								InvalidTupleSizeException, 
  								HFDiskMgrException,
  								HFBufMgrException,
  								IOException,
  								Exception
  {
      int answer = 0;
      PageId currentDirPageId = new PageId(_firstDirPageId.pid);
      PageId nextDirPageId = new PageId(0);
      HFPage currentDirPage = new HFPage();
      Page pageinbuffer = new Page();
      RID curDirEntRID;
      Directory record;
      HFPage curDataPage;
      
      
   /*-------------------------------------------------
    * 
    * 
    * Put your code here 
    * 
    * 
    * 
    --------------------------------------------------*/
      
      while( currentDirPageId.pid != 0 )
      {
    	  HFPage page = new HFPage();
    	  pinPage( currentDirPageId, page, false );
    	  currentDirPage = page;
    	  
    	  curDirEntRID = currentDirPage.firstRecord();
    	  
    	  while( curDirEntRID != null )
    	  {
    		  record = new Directory( currentDirPage.getRecord( curDirEntRID ).getTupleByteArray(), 0 );
    		  HFPage dataPage = new HFPage();
    		  pinPage( record.getPageId(), dataPage, false );
    		  curDataPage = dataPage;
    		  answer += curDataPage.recordCount();
    		  //unpin page
    		  unpinPage( record.getPageId(), false );
    		  curDirEntRID = currentDirPage.nextRecord( curDirEntRID );
    	  }
    	  
    	  //unpin page
    	  currentDirPageId = getNextPageId( currentDirPageId );
      }
      
      return 0;
      
  } // end of getRecCnt
  
  private PageId getNextPageId( PageId currentDirPageId ) throws HFBufMgrException, IOException {
	// TODO Auto-generated method stub.
	  //Get the next page id from the bufmgr
	  HFPage page = new HFPage();
	  pinPage( currentDirPageId, page, false );
      PageId nextPageId = page.getPrevPage();
      unpinPage( currentDirPageId, false );
      return nextPageId;
}

  
  private PageId getPrevPageId(PageId pageNo) throws HFBufMgrException, IOException {
		// TODO Auto-generated method stub
	  HFPage dirPage = new HFPage();
      pinPage( pageNo, dirPage, false);
      PageId nextPageId = dirPage.getPrevPage();
      unpinPage( pageNo, false) ;
      return nextPageId;
}

/** Insert record into file, return its Rid.
   *
   * @param recPtr pointer of the record
   * @param recLen the length of the record
   *
   * @return the rid of the record
 * @throws Exception 
   */
  public RID insertRecord( byte[] recPtr ) throws Exception
    {
      int recLen = recPtr.length;
      
      /*-------------------------------------------------
       * 
       * 
       * Put your code here
       * 
       * 
       * 
       --------------------------------------------------*/
      
      if( recLen > HFPage.getRecordSize() )
      {
    	  throw new SpaceNotAvailableException( null, "Unallowed record size" );
      }
      
      RID dirRid = null;
      
      RID dirRidEntry = getDirectorySpace( recLen );
      
      HFPage dirPage = new HFPage();
      pinPage( dirRidEntry.pageNo, dirPage, false);
      
      //get dir entry with reference to bytes so we can update it's free space.
      Tuple dirRecTuple = dirPage.returnRecord( dirRidEntry );
      Directory dirRec = new Directory( dirRecTuple.returnTupleByteArray(), dirRecTuple.getOffset() );
     
      //get data page
      HFPage dataPage = new HFPage();
      pinPage(dirRec.getPageId(), dataPage, false);
     
      //insert record into data page
      dirRid = dataPage.insertRecord(recPtr);
     
      //update dirPage
      dirRec.setFreeSpace(dataPage.available_space());
     
      //unpin pages.
      unpinPage( dirRec.getPageId(), true );
      unpinPage( dirPage.getCurPage(), true );
     
      return dirRid;
      
    }
  
  private RID getDirectorySpace( int lengthOfRecord )
  throws Exception
  {
      RID dirSlotRID = null;
      PageId currentDirPageId = _firstDirPageId;
     
      //Run loop until it has a place to put the record.
      while ( dirSlotRID == null )
      {
          //search the current dir page for an existing data page with space.
          dirSlotRID = searchDirectoryForNewSpace( currentDirPageId, lengthOfRecord);
          //didn't find a data page with space so try adding one.
          if ( dirSlotRID == null )
          {
              dirSlotRID = addDataPageToDirectory( currentDirPageId );
             
              //The directory page is full so move to next one.
              if ( dirSlotRID == null )
              {
                  currentDirPageId = getNextPageId( currentDirPageId );
                 
                  //There are no directory pages left so add a new one.
                  if ( currentDirPageId.pid == 0 )
                  {
                          currentDirPageId = addDirPage();
                  }
              }
          }
      }
      return dirSlotRID;
  }



/** Delete record from file with given rid.
   *
   * @exception InvalidSlotNumberException invalid slot number
   * @exception InvalidTupleSizeException invalid tuple size
   * @exception HFException heapfile exception
   * @exception HFBufMgrException exception thrown from bufmgr layer
   * @exception HFDiskMgrException exception thrown from diskmgr layer
   * @exception Exception other exception
   *
   * @return true record deleted  false:record not found
   */
  public boolean deleteRecord( RID rid ) throws InvalidSlotNumberException, 
	   											InvalidTupleSizeException, 
	   											HFException, 
	   											HFBufMgrException,
	   											HFDiskMgrException,
	   											Exception
  
    {
      boolean statusRecord = false;
      HFPage currentDirPage = new HFPage();
      PageId currentDirPageId = new PageId();
      RID currentDataPageRid = new RID();
      
      //This wasn't working right
      /*statusRecord = _findDataPage(rid,
			     currentDirPageId, currentDirPage, 
			     currentDataPageId, currentDataPage,
			     currentDataPageRid);
			     */
      
      /*-------------------------------------------------
       * 
       * 
       * Put your code here
       * 
       * 
       * 
       --------------------------------------------------*/
      
      
      //Look throughout the directory for the records datapage
      currentDataPageRid = rid;
      PageId dataPageId = currentDataPageRid.pageNo;
      RID currentDirEntryRID = new RID();
      Directory dirRec;
      while ( currentDirPageId.pid != 0 && currentDataPageRid == null )
      {
          pinPage(currentDirPageId, currentDirPage, false);
          currentDirEntryRID = currentDirPage.firstRecord();
         
          //loop through all entries in directory page.
          while ( currentDirEntryRID != null)
          {
              dirRec = new Directory( currentDirPage.getRecord( currentDataPageRid ).getTupleByteArray(), 0);
             
              //See if the entry matches the
              if ( dirRec.getPageId().pid == dataPageId.pid )
              {
            	  currentDataPageRid = currentDirEntryRID;
                  break;
              }
              currentDirEntryRID = currentDirPage.nextRecord(currentDirEntryRID);
          }
         
          unpinPage(currentDirPageId, false);
          currentDirPageId = getNextPageId(currentDirPageId);
      }
      
      if( currentDataPageRid != null )
      {
    	  statusRecord = true;
    	  
    	  HFPage pagedata = new HFPage();
    	  pinPage( rid.pageNo, pagedata, false );
    	  
    	  //delete the record
    	  pagedata.deleteRecord(rid);
          boolean isEmpty = pagedata.empty();
          unpinPage( rid.pageNo, true );
    	  
          if( isEmpty )
          {
        	  //free the page
        	  freePage( rid.pageNo );
        	  
        	  //update directory page.
              HFPage dirPage = new HFPage();
             
              pinPage( currentDataPageRid.pageNo, dirPage, false );
              dirPage.deleteRecord( currentDataPageRid );
              isEmpty = dirPage.empty();
              unpinPage( currentDataPageRid.pageNo, true );
              
              if ( isEmpty )
              {
                  PageId nextPageId = getNextPageId( currentDirEntryRID.pageNo );
                  PageId prevPageId = getPrevPageId( currentDirEntryRID.pageNo );
                 
                  //update pointers on either side.
                  if ( nextPageId.pid != 0 )
                          setNextPageId( nextPageId, prevPageId );
                  if ( prevPageId.pid != 0 )
                          setNextPageId( prevPageId, nextPageId );
                 
                  freePage( currentDirEntryRID.pageNo );
              }
          }
      }
      
      return statusRecord;
    }
  

  
  
  /** Updates the specified record in the heapfile.
   * @param rid: the record which needs update
   * @param newtuple: the new content of the record
   *
   * @exception InvalidSlotNumberException invalid slot number
   * @exception InvalidUpdateException invalid update on record
   * @exception InvalidTupleSizeException invalid tuple size
   * @exception HFException heapfile exception
   * @exception HFBufMgrException exception thrown from bufmgr layer
   * @exception HFDiskMgrException exception thrown from diskmgr layer
   * @exception Exception other exception
   * @return ture:update success   false: can't find the record
   */
  public boolean updateRecord(RID rid, Tuple newtuple) 
    throws InvalidSlotNumberException, 
	   InvalidUpdateException, 
	   InvalidTupleSizeException,
	   HFException, 
	   HFDiskMgrException,
	   HFBufMgrException,
	   Exception
    {
      boolean status;
      HFPage dirPage = new HFPage();
      PageId currentDirPageId = new PageId();
      HFPage dataPage = new HFPage();
      PageId currentDataPageId = new PageId();
      RID currentDataPageRid = new RID();
      
      /*status = _findDataPage(rid,
			     currentDirPageId, dirPage, 
			     currentDataPageId, dataPage,
			     currentDataPageRid);*/
      
      /*-------------------------------------------------
       * 
       * 
       * Put your code here
       * 
       * 
       * 
       --------------------------------------------------*/
      
      Tuple tuple = getRecord(rid);
      if (tuple != null)
      {
              tuple.tupleCopy(newtuple);
      }
      
      status = (tuple != null);
      
      return status;
    }
  
  
  /** Read record from file, returning pointer and length.
   * @param rid Record ID
   *
   * @exception InvalidSlotNumberException invalid slot number
   * @exception InvalidTupleSizeException invalid tuple size
   * @exception SpaceNotAvailableException no space left
   * @exception HFException heapfile exception
   * @exception HFBufMgrException exception thrown from bufmgr layer
   * @exception HFDiskMgrException exception thrown from diskmgr layer
   * @exception Exception other exception
   *
   * @return a Tuple. if Tuple==null, no more tuple
   */
  public  Tuple getRecord(RID rid) 
    throws InvalidSlotNumberException, 
	   InvalidTupleSizeException, 
	   HFException, 
	   HFDiskMgrException,
	   HFBufMgrException,
	   Exception
    {
      HFPage dataPage = new HFPage();
       
      /*-------------------------------------------------
       * 
       * 
       * Put your code here
       * 
       * 
       * 
       --------------------------------------------------*/
      
      //Pin dataPage specified by rid.pageNo
      pinPage( rid.pageNo, dataPage, false );
      //call getRecord on dataPage.
      Tuple tuple = dataPage.returnRecord(rid);
      //unpin page.
      unpinPage( rid.pageNo, false );
     
      return tuple;
    }
  
  
  /** Initiate a sequential scan.
   * @exception InvalidTupleSizeException Invalid tuple size
   * @exception IOException I/O errors
   *
   */
  public Scan openScan() 
    throws InvalidTupleSizeException,
	   IOException
    {
      Scan newscan = new Scan(this);
      return newscan;
    }
  
  
  /** Delete the file from the database.
   *
   * @exception InvalidSlotNumberException invalid slot number
   * @exception InvalidTupleSizeException invalid tuple size
   * @exception FileAlreadyDeletedException file is deleted already
   * @exception HFBufMgrException exception thrown from bufmgr layer
   * @exception HFDiskMgrException exception thrown from diskmgr layer
   * @exception IOException I/O errors
   */
  public void deleteFile()  
    throws InvalidSlotNumberException, 
	   FileAlreadyDeletedException, 
	   InvalidTupleSizeException, 
	   HFBufMgrException,
	   HFDiskMgrException,
	   IOException
    {
      if(_file_deleted ) 
   	throw new FileAlreadyDeletedException(null, "file alread deleted");
      
      
      // Mark the deleted flag (even if it doesn't get all the way done).
      _file_deleted = true;
      
      // Deallocate all data pages
      PageId currentDirPageId = new PageId();
      currentDirPageId.pid = _firstDirPageId.pid;
      PageId nextDirPageId = new PageId();
      nextDirPageId.pid = 0;
      Page pageinbuffer = new Page();
      HFPage currentDirPage =  new HFPage();
      Tuple atuple;
      
      /*-------------------------------------------------
       * 
       * 
       * Put your code here
       * 
       * 
       * 
       --------------------------------------------------*/
      
      nextDirPageId = getNextPageId(currentDirPageId);
      RID currentDirEntryRID = null;
      
      while (nextDirPageId.pid != 0)
      {
              pinPage( currentDirPageId, currentDirPage, false );
              nextDirPageId = currentDirPage.getNextPage();             
              currentDirEntryRID = currentDirPage.firstRecord();
             
              //in each dir page, free every data page.
              while ( currentDirEntryRID != null )
              {
                      freePage( currentDirEntryRID.pageNo );
                      currentDirEntryRID = currentDirPage.nextRecord( currentDirEntryRID );
              }
              
              unpinPage( currentDirPageId, false );
              freePage( currentDirPageId );
      }
     
      //delete entry.
      delete_file_entry( _fileName );
      
    }
  
  /**
   * short cut to access the pinPage function in bufmgr package.
   * @see bufmgr.pinPage
   */
  private void pinPage(PageId pageno, Page page, boolean emptyPage)
    throws HFBufMgrException {
    
    try {
      SystemDefs.JavabaseBM.pinPage(pageno, page, emptyPage);
    }
    catch (Exception e) {
      throw new HFBufMgrException(e,"Heapfile.java: pinPage() failed");
    }
    
  } // end of pinPage

  /**
   * short cut to access the unpinPage function in bufmgr package.
   * @see bufmgr.unpinPage
   */
  private void unpinPage(PageId pageno, boolean dirty)
    throws HFBufMgrException {

    try {
      SystemDefs.JavabaseBM.unpinPage(pageno, dirty);
    }
    catch (Exception e) {
      throw new HFBufMgrException(e,"Heapfile.java: unpinPage() failed");
    }

  } // end of unpinPage

  private void freePage(PageId pageno)
    throws HFBufMgrException {

    try {
      SystemDefs.JavabaseBM.freePage(pageno);
    }
    catch (Exception e) {
      throw new HFBufMgrException(e,"Heapfile.java: freePage() failed");
    }

  } // end of freePage

  private PageId newPage(Page page, int num)
    throws HFBufMgrException {

    PageId tmpId = new PageId();

    try {
      tmpId = SystemDefs.JavabaseBM.newPage(page,num);
    }
    catch (Exception e) {
      throw new HFBufMgrException(e,"Heapfile.java: newPage() failed");
    }

    return tmpId;

  } // end of newPage

  private PageId get_file_entry(String filename)
    throws HFDiskMgrException {

    PageId tmpId = new PageId();

    try {
      tmpId = SystemDefs.JavabaseDB.get_file_entry(filename);
    }
    catch (Exception e) {
      throw new HFDiskMgrException(e,"Heapfile.java: get_file_entry() failed");
    }

    return tmpId;

  } // end of get_file_entry

  private void add_file_entry(String filename, PageId pageno)
    throws HFDiskMgrException {

    try {
      SystemDefs.JavabaseDB.add_file_entry(filename,pageno);
    }
    catch (Exception e) {
      throw new HFDiskMgrException(e,"Heapfile.java: add_file_entry() failed");
    }

  } // end of add_file_entry

  private void delete_file_entry(String filename)
    throws HFDiskMgrException {

    try {
      SystemDefs.JavabaseDB.delete_file_entry(filename);
    }
    catch (Exception e) {
      throw new HFDiskMgrException(e,"Heapfile.java: delete_file_entry() failed");
    }

  } // end of delete_file_entry

  /**
   * 
   * @author mattaiss
   *
   */
  class Directory{
	  
	public static final int RECORD_LENGTH = 6;//<-- not sure on this length
	public byte [] data = new byte[ RECORD_LENGTH ];
	public int offset = 0;
	 
	public Directory( byte[] tupleByteArray, int offset ) {
		// TODO Auto-generated constructor stub
		this.offset = offset;
		this.data = tupleByteArray;		
	}
	
	public Directory( PageId dataPageId, short available_space, int offset ) throws IOException {
		// TODO Auto-generated constructor stub
		  this.offset = offset;
	      setPageId( dataPageId );
	      setFreeSpace( available_space );
	}

	private void setPageId( PageId dataPageId ) throws IOException {
		// TODO Auto-generated method stub
		Convert.setIntValue( dataPageId.pid, 0 + offset, data );
	}

	public void setFreeSpace( int available_space ) throws IOException {
		// TODO Auto-generated method stub
    	  Convert.setShortValue( (short) available_space, 4 + offset, data);
	}

	public int getFreeSpace() throws IOException {
		// TODO Auto-generated method stub
    	  return Convert.getShortValue( 4 + offset, data );
	}

	public PageId getPageId() throws IOException {
			// TODO Auto-generated method stub
    	  return new PageId( Convert.getIntValue(0 + offset, data) );
      }
      
  }
  
  
  private PageId addDirPage() throws HFBufMgrException, IOException {
		// TODO Auto-generated method stub
	PageId lastPageId = getLastDirectoryPageId();
    
    //make new page
    HFPage newDirPage = new HFPage();
    
    PageId newDirPageId = newPage( newDirPage, 1 );
    
    newDirPage.setCurPage( newDirPageId );
    
    newDirPage.setPrevPage( lastPageId );
    newDirPage.setFreeSpace();
    
    //unpin page
    unpinPage(newDirPageId, true);
   
    setNextPageId(lastPageId, newDirPageId);
   
    return newDirPageId;
}


private PageId getLastDirectoryPageId() throws HFBufMgrException, IOException {
	// TODO Auto-generated method stub
	PageId lastPageId = _firstDirPageId;
    while( getNextPageId( lastPageId ).pid != 0)
    {
            lastPageId = getNextPageId(lastPageId);
    }
    return lastPageId;
}

/**
 * Set the next Page ID in the directory
 * @param lastPageId
 * @param newDirPageId
 * @throws HFBufMgrException
 * @throws IOException
 */
private void setNextPageId( PageId lastPageId, PageId newDirPageId ) throws HFBufMgrException, IOException {
	// TODO Auto-generated method stub
	HFPage dirPage = new HFPage();
	pinPage( lastPageId, dirPage, false );
    dirPage.setNextPage( newDirPageId );
    unpinPage( lastPageId, true );
}

private RID addDataPageToDirectory( PageId currentDirPageId ) throws HFBufMgrException, IOException {
	// TODO Auto-generated method stub
	RID retValue = new RID();
    
    HFPage dirPage = new HFPage(); 
    pinPage( currentDirPageId, dirPage, false );
   
    //See if there's space for a new data page.
    if ( dirPage.isFull( Directory.RECORD_LENGTH ) )
    {
        //Make new data page.
        HFPage dataPage = new HFPage();
        PageId dataPageId = newPage(dataPage, 1);
        
        dataPage.setCurPage( dataPageId );
        dataPage.setFreeSpace();
       
        //update directory page.
        Directory dirRec = new Directory( dataPageId, dataPage.available_space(), 0 );
        retValue = dirPage.insertRecord( dirRec.data );
       
        //free up data page.
        unpinPage(dataPageId, true);
    }
    
    //free up dir page.
    unpinPage( currentDirPageId, true );
   
    return retValue;
}

private RID searchDirectoryForNewSpace( PageId dirPageId, int length ) throws HFBufMgrException, IOException, InvalidSlotNumberException {
	// TODO Auto-generated method stub
	HFPage newPage = new HFPage();
	pinPage( dirPageId, newPage, false );
	
    RID returnValue = null;
    RID currentRID = newPage.firstRecord();
   
    //loop through all records in this directory page.
    while ( currentRID != null )
    {
        Tuple dirRec = newPage.getRecord( currentRID );
        Directory record = new Directory( dirRec.getTupleByteArray(), dirRec.getOffset() );
       
        if ( ( length + HFPage.USED_PTR ) <= record.getFreeSpace())
        {
                returnValue = currentRID;
                break;
        }
        else
        {
                currentRID = newPage.nextRecord( currentRID );
        }
    }
   
    unpinPage( dirPageId, false );
   
    return returnValue;
}
  
}// End of HeapFile 

