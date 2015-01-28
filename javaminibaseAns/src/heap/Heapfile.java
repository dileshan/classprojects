package heap;

import java.io.*;
import java.util.*;
import java.lang.*;
import heap.*;
import bufmgr.*;
import diskmgr.*;
import global.*;
import chainexception.*;

/*FROM FORUM:
 * Basically if the name is null you must create a temp name ("tempHeapFile"), which you will delete when the
 * heapfile object is destroyed (override finalize()). If a name is provided call the DB get_file_entry which
 * will return the heap file directory page pid associated with the entry. If there is no pid entry found
 * (get_file_entry returns null) you must create the file entry by creating a new page with the buffer manager
 * and calling the DB add_file_entry function with the name and pid of the newly created page.
 * No other initialization is needed as far as I can tell.
 */

/* Store records in directory pages with values: PageId.pid (int), freeSpace(short)
 * so we don't have to get each page to check its freespace.
 */

public class Heapfile implements GlobalConst{
       
        //constant values from http://www-users.itlabs.umn.edu/classes/Spring-2008/csci5708/index.php?page=.//labs/HeapFile/javadoc/constant-values
        public static final int TEMP = 0;
        public static final int ORDINARY = 1;
        public static final String TEMP_NAME = "tempHeapFile";
       
        private String fileName = "";
        private PageId headPageId;
       
        /**
         * Initialize. A null name produces a temporary heapfile which will be deleted by the destructor.
         * If the name already denotes a file, the file is opened; otherwise, a new empty file is created.
         * @param name
         * @throws java.io.IOException
         */
        public Heapfile(java.lang.String name)
    throws java.io.IOException, Exception {
                //Make temp file.
                if (name == null)
                {
                        name = TEMP_NAME;
                }
                fileName = name;
               
                try
                {
                        //See if file exists.
                        headPageId = SystemDefs.JavabaseDB.get_file_entry(fileName);
                       
                        //If null, create new page.
                        if (headPageId == null)
                        {
                                //make head page.
                                HFPage headPage = new HFPage();
                                headPageId = SystemDefs.JavabaseBM.newPage(headPage, 1);
                                headPage.setCurPage(headPageId);
                                headPage.setDefaultFreeSpace();
                                unpinPage(headPageId, true);
                               
                                //add entry
                                SystemDefs.JavabaseDB.add_file_entry(fileName, headPageId);
                        }
                }
                catch (Exception ex)
                {
                        throw ex;
                }
   }
       
        /**
         * Return number of records in file.
         * @return
         * @throws java.io.IOException
         */
        public int getRecCnt()throws java.io.IOException, Exception 
        {
                int recordCount = 0;
                PageId currentDirPageId = headPageId;
                HFPage currentDirPage;
                RID currentDirEntryRID;
                DirectoryRecord dirRec;
               
                HFPage currentDataPage;
               
                //loop through all directory pages.
                while (currentDirPageId.pid != 0)
                {
                        currentDirPage = pinPage(currentDirPageId, false);
                        currentDirEntryRID = currentDirPage.firstRecord();
                       
                        //loop through all data pages.
                        while (currentDirEntryRID != null)
                        {
                                dirRec = new DirectoryRecord(currentDirPage.getRecord(currentDirEntryRID).getTupleByteArray(), 0);
                                currentDataPage = pinPage(dirRec.getPageId(), false);
                                recordCount += currentDataPage.recordCount();
                                unpinPage(dirRec.getPageId(), false);
                               
                                currentDirEntryRID = currentDirPage.nextRecord(currentDirEntryRID);
                        }
                       
                        unpinPage(currentDirPageId, false);
                        currentDirPageId = getNextPageId(currentDirPageId);
                }
                return recordCount;
        }
       
        /**
         * Insert record into file, return its Rid.
         * @param recPtr pointer of the record
         * @return the rid of the record
         * @throws java.io.IOException
         * @throws ChainException
         */
        public RID insertRecord(byte[] recPtr)
    throws java.io.IOException, SpaceNotAvailableException, ChainException, Exception {
                //Check if record is > max possible size.
                if (recPtr.length > HFPage.getMaxRecordSize())
                {
                        throw new SpaceNotAvailableException(null, "Record bigger than max allowed.");
                }
               
                RID newDataRID = null;
               
                //get directory record with datapage.
                RID dirEntryRID = getDirectoryEntryWithSpace(recPtr.length);
                HFPage dirPage = pinPage(dirEntryRID.pageNo, false);
               
                //get dir entry with reference to bytes so we can update it's free space.
                Tuple dirRecTuple = dirPage.returnRecord(dirEntryRID);
                DirectoryRecord dirRec = new DirectoryRecord(dirRecTuple.returnTupleByteArray(), dirRecTuple.getOffset());
               
                //get data page
                HFPage dataPage = pinPage(dirRec.getPageId(), false);
               
                //insert record into data page
                newDataRID = dataPage.insertRecord(recPtr);
               
                //update dirPage
                dirRec.setFreeSpace(dataPage.available_space());
               
                //unpin pages.
                unpinPage(dirRec.getPageId(), true);
                unpinPage(dirPage.getCurPage(), true);
               
                return newDataRID;
        }
       
        /**
         * Delete record from file with given rid.
         * @param rid
         * @return true record deleted false:record not found
         * @throws java.lang.Exception
         */
        public boolean deleteRecord(RID rid)
    throws java.lang.Exception {
               
                boolean foundRecord = false;
               
                //Find record's dataPage's respective directory record.
                RID directoryEntryRID = scanDirectoryForDataPage(rid.pageNo);
               
                if (directoryEntryRID != null)
                {
                        foundRecord = true;
                       
                        HFPage dataPage = pinPage(rid.pageNo, false);
                       
                        //delete record.
                        dataPage.deleteRecord(rid);
                        boolean empty = dataPage.empty();
                        unpinPage(rid.pageNo, true);
                       
                        if (empty)
                        {
                                //delete data page if it's empty
                                SystemDefs.JavabaseBM.freePage(rid.pageNo);
                               
                                //update directory page.
                                HFPage dirPage = pinPage(directoryEntryRID.pageNo, false);
                                dirPage.deleteRecord(directoryEntryRID);
                                empty = dirPage.empty();
                                unpinPage(directoryEntryRID.pageNo, true);
                               
                                //remove directory page if it's empty.
                                if (empty)
                                {
                                        PageId nextPageId = getNextPageId(directoryEntryRID.pageNo);
                                        PageId prevPageId = getPrevPageId(directoryEntryRID.pageNo);
                                       
                                        //update pointers on either side.
                                        if (nextPageId.pid != 0)
                                        {
                                                setPrevPageId(nextPageId, prevPageId);
                                        }
                                        if (prevPageId.pid != 0)
                                        {
                                                setNextPageId(prevPageId, nextPageId);
                                        }
                                       
                                        SystemDefs.JavabaseBM.freePage(directoryEntryRID.pageNo);
                                }
                        }                      
                }
               
                return foundRecord;
        }


        /**
         * Updates the specified record in the heapfile
         * @param rid the record which needs update
         * @param newtuple the new content of the record
         * @return true:update success false: can't find the record
         * @throws java.lang.Exception
         */
        public boolean updateRecord(RID rid,
            Tuple newtuple)
     throws heap.InvalidUpdateException, java.lang.Exception {
                Tuple tuple = getRecord(rid);
                if (tuple != null)
                {
                        tuple.tupleCopy(newtuple);
                }
                return (tuple != null);
        }
       
        /**
         * Read record from file, returning pointer and length
         * @param rid Record ID
         * @return a Tuple. if Tuple==null, no more tuple
         * @throws java.lang.Exception
         */
        public Tuple getRecord(RID rid)
    throws java.lang.Exception {
               
                //Pin dataPage specified by rid.pageNo
                HFPage dataPage = pinPage(rid.pageNo, false);
               
                //call getRecord on dataPage.
                Tuple tuple = dataPage.returnRecord(rid);
               
                //unpin page.
                unpinPage(rid.pageNo, false);
               
                return tuple;
        }
       
        /**
         * Initiate a sequential scan.
         * @return
         * @throws java.io.IOException
         */
        public Scan openScan()
    throws java.io.IOException, Exception {
                return new Scan(this);
        }
       
        /**
         * Delete the file from the database.
         * @throws java.io.IOException
         */
        public void deleteFile()
    throws java.io.IOException, Exception {
                try
                {
                        PageId currentDirPageId = headPageId;
                        HFPage currentDirPage;
                        PageId nextDirPageId = getNextPageId(currentDirPageId);
                        RID currentDirEntryRID = null;
                       
                        //iterate through pages and delete each directory page.
                        while (nextDirPageId.pid != 0)
                        {
                                currentDirPage = pinPage(currentDirPageId, false);
                                nextDirPageId = currentDirPage.getNextPage();
                               
                                currentDirEntryRID = currentDirPage.firstRecord();
                               
                                //in each dir page, free every data page.
                                while (currentDirEntryRID != null)
                                {
                                        SystemDefs.JavabaseBM.freePage(currentDirEntryRID.pageNo);
                                        currentDirEntryRID = currentDirPage.nextRecord(currentDirEntryRID);
                                }
                                unpinPage(currentDirPageId, false);
                                SystemDefs.JavabaseBM.freePage(currentDirPageId);
                        }
                       
                        //delete entry.
                        SystemDefs.JavabaseDB.delete_file_entry(fileName);
                }
                catch (Exception ex)
                {
                        throw ex;
                }
        }
       
        /**
         * Gets the headPageId.
         * @return
         */
        public PageId getHeadPageId()
        {
                return headPageId;
        }
       
        public void finalize()
        throws java.io.IOException, Exception
        {
                if (fileName.equals(TEMP_NAME))
                {
                        deleteFile();
                }
        }
       
        /**************************************************
         * UTILITY FUNCTIONS
         * NOTE: ALL ARE ATOMIC WITH RESPECT TO PINNING & UNPINNING.
         ***************************************************
         */
       
        /**
         * Searches though all directory pages for space and creates it if needed.
         * @param lengthOfRecord size of the record trying to insert
         * @return the directory slot RID that has space.
         */
        private RID getDirectoryEntryWithSpace(int lengthOfRecord)
        throws Exception
        {
                RID dirSlotRID = null;
                PageId currentDirPageId = headPageId;
               
                //Run loop until it has a place to put the record.
                while (dirSlotRID == null)
                {
                        //search the current dir page for an existing data page with space.
                        dirSlotRID = searchDirectoryPageForSpace(currentDirPageId, lengthOfRecord);
                        //didn't find a data page with space so try adding one.
                        if (dirSlotRID == null)
                        {
                                dirSlotRID = addDataPage(currentDirPageId);
                               
                                //The directory page is full so move to next one.
                                if (dirSlotRID == null)
                                {
                                        currentDirPageId = getNextPageId(currentDirPageId);
                                       
                                        //There are no directory pages left so add a new one.
                                        if (currentDirPageId.pid == 0)
                                        {
                                                currentDirPageId = addDirectoryPage();
                                        }
                                }
                        }
                }
                return dirSlotRID;
        }
       
        /**
         * Finds the first data page in the directory page with enough space.
         * Returns null if no existing data page has space.
         * @param directoryPageId
         * @param lengthOfRecord
         * @return the RID of the directory record.
         */
        private RID searchDirectoryPageForSpace(PageId directoryPageId, int lengthOfRecord)
        throws Exception
        {
                HFPage dirPage = pinPage(directoryPageId, false);
                RID returnValue = null;
                RID currentRID = dirPage.firstRecord();
               
                //loop through all records in this directory page.
                while (currentRID != null)
                {
                        Tuple dirRecTuple = dirPage.getRecord(currentRID);
                        DirectoryRecord dirRec = new DirectoryRecord(dirRecTuple.getTupleByteArray(), dirRecTuple.getOffset());
                       
                        if ((lengthOfRecord + HFPage.USED_PTR) <= dirRec.getFreeSpace())
                        {
                                returnValue = currentRID;
                                break;
                        }
                        else
                        {
                                currentRID = dirPage.nextRecord(currentRID);
                        }
                }
               
                unpinPage(directoryPageId, false);
               
                return returnValue;
        }
       
        /**
         * Adds a new data page to the directory page.
         * @param DirectoryPageId Directory page to add the data page to.
         * @return Directory slot RID of the next data page.  Null if not enough space.
         */
        private RID addDataPage(PageId directoryPageId)
        throws Exception
        {
                RID retValue = null;
               
                HFPage dirPage = pinPage(directoryPageId, false);
               
                //See if there's space for a new data page.
                if (dirPage.willFit(DirectoryRecord.LENGTH_OF_DIR_RECORD))
                {
                        //Make new data page.
                        HFPage dataPage = new HFPage();
                        PageId dataPageId = SystemDefs.JavabaseBM.newPage(dataPage, 1);
                        dataPage.setCurPage(dataPageId);
                        dataPage.setDefaultFreeSpace();
                       
                        //update directory page.
                        DirectoryRecord dirRec = new DirectoryRecord(dataPageId, dataPage.available_space(), 0);
                        retValue = dirPage.insertRecord(dirRec.data);
                       
                        //free up data page.
                        unpinPage(dataPageId, true);
                }
                //free up dir page.
                unpinPage(directoryPageId, true);
               
                return retValue;
        }
       
        /**
         * Adds a directory page to the end of the linked list.
         * @return PageId of new dir page.
         * @throws Exception
         */
        private PageId addDirectoryPage()
        throws Exception
        {
                PageId lastPageId = getLastDirectoryPageId();
               
                //make new page
                HFPage newDirPage = new HFPage();
                PageId newDirPageId = SystemDefs.JavabaseBM.newPage(newDirPage, 1);
                newDirPage.setCurPage(newDirPageId);
                newDirPage.setPrevPage(lastPageId);
                newDirPage.setDefaultFreeSpace();
                unpinPage(newDirPageId, true);
               
                setNextPageId(lastPageId, newDirPageId);
               
                return newDirPageId;
        }
       
        /**
         * gets the PageId of the last directory page.
         * @return
         */
        private PageId getLastDirectoryPageId()
        throws Exception
        {
                PageId lastPageId = headPageId;
                while(getNextPageId(lastPageId).pid != 0)
                {
                        lastPageId = getNextPageId(lastPageId);
                }
                return lastPageId;
        }
       
        /**
         * Gets the next page value of the given directory Page.
         * @param dirPageId
         * @return
         * @throws Exception
         */
        private PageId getNextPageId(PageId dirPageId)
        throws Exception
        {
                HFPage dirPage = pinPage(dirPageId, false);
                PageId nextPageId = dirPage.getNextPage();
                unpinPage(dirPageId, false);
                return nextPageId;
        }
       
        /**
         * Gets the prev page value of the given directory page.
         * @param dirPageId
         * @return
         * @throws Exception
         */
        private PageId getPrevPageId(PageId dirPageId)
        throws Exception
        {
                HFPage dirPage = pinPage(dirPageId, false);
                PageId nextPageId = dirPage.getPrevPage();
                unpinPage(dirPageId, false);
                return nextPageId;
        }
       
        /**
         * Sets the prevPage value on the directory page.
         * @param dirPageId
         * @param value
         * @throws Exception
         */
        private void setPrevPageId(PageId dirPageId, PageId value)
        throws Exception
        {
                HFPage dirPage = pinPage(dirPageId, false);
                dirPage.setPrevPage(value);
                unpinPage(dirPageId, true);
        }
       
        /**
         * Sets the nextPage value on the directory page.
         * @param dirPageId
         * @param value
         * @throws Exception
         */
        private void setNextPageId(PageId dirPageId, PageId value)
        throws Exception
        {
                HFPage dirPage = pinPage(dirPageId, false);
                dirPage.setNextPage(value);
                unpinPage(dirPageId, true);
        }
       
        public void dumpDirPage(PageId dirPageId)
        throws Exception
        {
                HFPage dirPage = pinPage(dirPageId, false);
                System.out.println("Dumping directory page: " + dirPageId.pid);
                System.out.println("Slot Count: " + dirPage.getSlotCnt());
                System.out.println("Free Space: " + dirPage.available_space());
                System.out.println("Type: " + dirPage.getType());
                System.out.println("Prev Page Id: " + dirPage.getPrevPage());
                System.out.println("Next Page Id: " + dirPage.getNextPage());
                System.out.println("Cur Page Id: " + dirPage.getCurPage());
                System.out.println("");
               
                RID rid = dirPage.firstRecord();
                Tuple tempTuple;
                DirectoryRecord dirrec;
                while(rid  != null)
                {
                        tempTuple = dirPage.getRecord(rid);
                        dirrec = new DirectoryRecord(tempTuple.getTupleByteArray(), tempTuple.getOffset());
                        System.out.println("Slot #: " + rid.slotNo);
                        System.out.println("   Length: " + dirPage.getSlotLength(rid.slotNo));
                        System.out.println("   Data: PageId: " + dirrec.getPageId() + " FreeSpace: " + dirrec.getFreeSpace());
                        rid = dirPage.nextRecord(rid);
                }
                System.out.println("");
                unpinPage(dirPageId, false);
               
        }
       
        /**
         * Looks through the directory for the directory entry record that references the given data page
         * @param dataPageId dataPageId to look for
         * @return directory entry record's RID or null if dataPage not found.
         */
        public RID scanDirectoryForDataPage(PageId dataPageId)
        throws Exception
        {
                RID directoryEntryRID = null;
                PageId currentDirPageId = headPageId;
                HFPage currentDirPage;
                RID currentDirEntryRID;
                DirectoryRecord dirRec;
               
                //loop through all directory pages.
                while (currentDirPageId.pid != 0 && directoryEntryRID == null)
                {
                        currentDirPage = pinPage(currentDirPageId, false);
                        currentDirEntryRID = currentDirPage.firstRecord();
                       
                        //loop through all entries in directory page.
                        while (currentDirEntryRID != null)
                        {
                                dirRec = new DirectoryRecord(currentDirPage.getRecord(currentDirEntryRID).getTupleByteArray(), 0);
                               
                                //See if the entry matches the
                                if (dirRec.getPageId().pid == dataPageId.pid)
                                {
                                        directoryEntryRID = currentDirEntryRID;
                                        break;
                                }
                                currentDirEntryRID = currentDirPage.nextRecord(currentDirEntryRID);
                        }
                        
                        unpinPage(currentDirPageId, false);
                        currentDirPageId = getNextPageId(currentDirPageId);
                }
               
                return directoryEntryRID;
        }
       
        /*
         * THESE ARE TO MAKE EXCEPTION HANDLING (AND LIFE) EASIER:
         */
        public static HFPage pinPage(PageId pgid, boolean empty)
        throws Exception
        {
                HFPage page = new HFPage();
                try
                {
                        SystemDefs.JavabaseBM.pinPage(pgid, page, empty);
                }
                catch (Exception ex)

                {
                        throw ex;
                }
                return page;
        }
        public static void unpinPage(PageId pgid, boolean dirty)
        throws Exception
        {
                try
                {
                        SystemDefs.JavabaseBM.unpinPage(pgid, dirty);
                }
                catch (Exception ex)
                {
                        throw ex;
                }
        }
}


class DirectoryRecord{
        public static final int LENGTH_OF_DIR_RECORD = 6; //pid(4) + freeSpace(2);
        public byte[] data = new byte[LENGTH_OF_DIR_RECORD];
       
        public int offset = 0;
       
        public DirectoryRecord(PageId pgid, short freeSpace, int offset)
        throws java.io.IOException{
                this.offset = offset;
                setPageId(pgid);
                setFreeSpace(freeSpace);
        }
       
        public DirectoryRecord(byte[] rec, int offset)
        {
                this.offset = offset;
                data = rec;
        }
       
        public void setPageId(PageId pgid)
        throws java.io.IOException
        {
                Convert.setIntValue(pgid.pid, 0 + offset, data);
        }
       
        public void setFreeSpace(short freeSpace)
        throws java.io.IOException
        {
                Convert.setShortValue(freeSpace, 4 + offset, data);
        }
       
        public PageId getPageId()
        throws java.io.IOException
        {
                return new PageId(Convert.getIntValue(0 + offset, data));
        }
       
        public short getFreeSpace()
        throws java.io.IOException
        {
                return Convert.getShortValue(4 + offset, data);
        }
}
