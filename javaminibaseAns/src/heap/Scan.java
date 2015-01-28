package heap;

import java.io.*;
import java.util.*;
import java.lang.*;
import heap.*;
import bufmgr.*;
import diskmgr.*;
import global.*;
import chainexception.*;

public class Scan implements GlobalConst{
        
        private Heapfile heapfile = null;
        
        private HFPage currentDirectoryPage = null;
        private RID directoryEntryRID = null; //the current location in the directory page.
        
        private HFPage currentDatapage = null;
        private RID dataRecordRID = null; //the current location in the data page.
        
        /**
         * The constructor pins the first directory page in the file and 
         * initializes its private data members from the private data member from hf 
         * @param hf A HeapFile object 
         * @throws java.io.IOException
         */
        public Scan(Heapfile hf)
    throws java.io.IOException, Exception {     
                
                heapfile = hf;
                
                //get/pin first directory page.
                currentDirectoryPage = Heapfile.pinPage(hf.getHeadPageId(), false);
                directoryEntryRID = currentDirectoryPage.firstRecord();
                
                //get/pin first data page in directory.
                if (!currentDirectoryPage.empty())
                {
                        Tuple tempTuple = currentDirectoryPage.getRecord(directoryEntryRID);
                        DirectoryRecord dataRec = new DirectoryRecord(tempTuple.getTupleByteArray(), 0);
                        
                        currentDatapage = Heapfile.pinPage(dataRec.getPageId(), false);
                }
        }
        
        /**
         * Retrieve the next record in a sequential scan 
         * @param rid Record ID of the record
         * @return the Tuple of the retrieved record
         * @throws java.io.IOException
         */
        public Tuple getNext(RID rid)
    throws java.io.IOException, Exception {
                
                Tuple nextTuple = null;
                
                //get first record if the pointer isn't initilized.
                if (dataRecordRID == null)
                {
                        dataRecordRID = currentDatapage.firstRecord();
                }
                else
                {
                        //look for next row in dataPage.
                        dataRecordRID = currentDatapage.nextRecord(dataRecordRID);
                }
                
                //if none, go to next data page in directory page.
                while (dataRecordRID == null)
                {
                        Heapfile.unpinPage(currentDatapage.getCurPage(), true);
                        directoryEntryRID = currentDirectoryPage.nextRecord(directoryEntryRID); //next dir page
                        
                        //if none, go to first data page in next directory page.
                        if (directoryEntryRID == null)
                        {
                                PageId nextDirPageId = currentDirectoryPage.getNextPage();
                                Heapfile.unpinPage(currentDirectoryPage.getCurPage(), true);
                                
                                //if no more directory pages, reset all pointers and break.
                                if (nextDirPageId.pid == 0)
                                {
                                        currentDirectoryPage = null;
                                        directoryEntryRID = null;
                                        currentDatapage = null;
                                        dataRecordRID = null;
                                        break;
                                }
                                
                                //Get directory page and first data page in it.
                                Heapfile.pinPage(nextDirPageId, false);
                                directoryEntryRID = currentDirectoryPage.firstRecord();
                        }
                        
                        //pin next data page
                        Tuple tempTuple = currentDirectoryPage.getRecord(directoryEntryRID);
                        DirectoryRecord dataRec = new DirectoryRecord(tempTuple.getTupleByteArray(), 0);
                        
                        //get first record
                        currentDatapage = Heapfile.pinPage(dataRec.getPageId(), false);
                        dataRecordRID = currentDatapage.firstRecord();
                }
                if (dataRecordRID != null)
                {
                        nextTuple = currentDatapage.getRecord(dataRecordRID);
                        rid.pageNo.pid= dataRecordRID.pageNo.pid;
                        rid.slotNo = dataRecordRID.slotNo;
                }
                
                return nextTuple;
        }
        
        /**
         * Position the scan cursor to the record with the given rid.
         * @param rid Record ID of the given record 
         * @return true if successful, false otherwise. 
         * @throws java.io.IOException
         */
        public boolean position(RID rid)
    throws java.io.IOException, Exception {
                boolean success = false;        
                
                //check if record exists first.
                if (heapfile.getRecord(rid) != null)
                {
                        //unpin old pages.
                        Heapfile.unpinPage(currentDirectoryPage.getCurPage(), true);
                        Heapfile.unpinPage(currentDatapage.getCurPage(), true);
                        
                        //get directory page
                        directoryEntryRID = heapfile.scanDirectoryForDataPage(rid.pageNo);
                        currentDirectoryPage = Heapfile.pinPage(directoryEntryRID.pageNo, false);
                        
                        //get data page
                        currentDatapage = Heapfile.pinPage(rid.pageNo, false);
                        dataRecordRID.pageNo.pid = rid.pageNo.pid;
                        dataRecordRID.slotNo = rid.slotNo;
                        
                        success = true;
                }
                return success;
        }

        /**
         * Closes the Scan object 
         */
        public void closescan()  {      
                try
                {
                        //unpin directory page
                        Heapfile.unpinPage(directoryEntryRID.pageNo, true);
                }
                catch (Exception ex)
                {
                        
                }
                try
                {
                        //unpin data page
                        Heapfile.unpinPage(dataRecordRID.pageNo, true);
                }
                catch (Exception ex)
                {
                        
                }
                currentDirectoryPage = null;
                directoryEntryRID = null;
                currentDatapage = null;
                dataRecordRID = null;
        }
}
