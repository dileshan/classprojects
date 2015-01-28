package heap;

import java.io.*;
import java.util.*;
import java.lang.*;
import heap.*;
import bufmgr.*;
import diskmgr.*;
import global.*;
import chainexception.*;

public class HFPage extends Page implements GlobalConst{

        //constant values from http://www-users.itlabs.umn.edu/classes/Spring-2008/csci5708/index.php?page=.//labs/HeapFile/javadoc/constant-values
        public static final int SIZE_OF_SLOT = 4;
        public static final int USED_PTR = 2; //size of entry in slot directory.
       
        //Offsets in byte array.
        public static final int SLOT_CNT = 0; //int
        public static final int FREE_SPACE = 4; //short
        public static final int TYPE = 6; //short
        public static final int PREV_PAGE = 8; //int
        public static final int NEXT_PAGE = 12;  //int
        public static final int CUR_PAGE = 16; //int
        public static final int DPFIXED = 20; //start of data
       
        public static final int INVALID_SLOT = -1;
        public static final int EMPTY_SLOT = -1;
       
       
        /**
         * Default constructor
         */
        public HFPage() throws java.io.IOException {
                setDefaultFreeSpace();
        }
       
        /**
         * Constructor of class HFPage open a HFPage and make this HFpage piont to the given page
         * @param page - the given page in Page type
         */
        public HFPage(Page page){
                data = page.getpage();
        }
       
        /**
         * Constructor of class HFPage open a existed hfpage
         * @param apage - a page in buffer pool
         */
        public void openHFpage(Page apage) {
                data = apage.getpage();
        }
       
        /**
         * Constructor of class HFPage initialize a new page
         * @param pageNo - the page number of a new page to be initialized
         * @param apage - the Page to be initialized
         * @throws java.io.IOException - I/O errors
         */
        public void init(PageId pageNo,
            Page apage)
     throws java.io.IOException {
                data = apage.getpage();
                this.setCurPage(pageNo);
                setFreeSpace((short)(MAX_SPACE - DPFIXED));
        }
       
        /**
         * sets slot contents
         * @param slotno - the slot number
         * @param length - length of record the slot contains
         * @param offset - offset of record
         * @throws java.io.IOException - I/O errors
         */
        public void setSlot(int slotno,
            int length,
            int offset)
     throws java.io.IOException {
                throw new UnsupportedOperationException("Not implemented.  Where does the data come from to 'set the contents'??");
        }
       
        /**
         *
         * @param slotno - slot number
         * @return - the length of record teh slots contains
         * @throws java.io.IOException - I/O errors
         */
        public short getSlotLength(int slotno)
    throws java.io.IOException {
                short startVal, endVal;
                startVal = abs(getSlotDirValue(slotno));
                if (slotno == 0)
                        endVal = MAX_SPACE;
                else
                        endVal = abs(getSlotDirValue(slotno - 1));
                return (short)abs((short)(startVal - endVal));
        }
       
        /**
         *
         * @param slotno - slot number
         * @return the offset of record the given slot contains
         * @throws java.io.IOException - I/O errors
         */
        public short getSlotOffset(int slotno)
    throws java.io.IOException {
                //the offset stored in the slot directory.
                return abs(getSlotDirValue(slotno));
        }
       
        /**
         * inserts a new record onto the page, returns RID of this record
         * @param record - a record to be inserted
         * @return - RID of record, null if sufficient space does not exist
         * @throws java.io.IOException - I/O errors
         */
        public RID insertRecord(byte[] record)
    throws java.io.IOException {
                RID rid = null;
                short len = (short)record.length;
               
                if (willFit(len))  //enough space for record
                {
                        short startFreeSpace = getStartOfFreeSpace();
                        short writeLocation = (short)(startFreeSpace - len);
                       
                        //create slot directory entry.
                        insertInSlotDirectory(writeLocation);
                       
                        //write record.
                        writeRecord(record, writeLocation);
                       
                        //make rid
                        rid = new RID(getCurPage(), getSlotCnt()); //rid is <pageid, slotNo>
                        setSlotCnt((short)(getSlotCnt() + 1)); //increment slot count.
                       
                        //update freespace
                        setFreeSpace((short)(getFreeSpace() - len - USED_PTR));
                }
                return rid;
        }
       
        /**
         * delete the record with the specified rid
         * @param rid - the record ID
         * @throws java.io.IOException - I/O errors
         */
        public void deleteRecord(RID rid)
    throws java.io.IOException {
                //To indicate a record has been deleted, turn it's slot directory value negative.
                //The value is still needed to know the endpoint of the record after the deleted one.
               
                //just update slot directory value if slot exists
                if (rid.slotNo < getSlotCnt())
                {
                        setSlotDeleted(rid.slotNo);
                }
        }
       
        /**
         *
         * @return - RID of first record on page, null if page contains no records.
         * @throws java.io.IOException - I/O errors
         */
        public RID firstRecord()
    throws java.io.IOException {
                RID rid = null;
                if (getSlotCnt() > 0)
                {
                        int i = 0;
                        short slotValue;
                        //loop through until it finds a non-deleted record.
                        while (i < getSlotCnt())
                        {
                                slotValue = getSlotDirValue(i);
                                //Found non-deleted record.
                                if (!slotIsDeleted(slotValue))
                                {
                                        rid = new RID(getCurPage(), i); //rid is <pageID, slotNo>
                                        break;
                                }
                                i++;
                        }
                }
                return rid;
        }
       
        /**
         *
         * @param curRid - current record ID
         * @return - RID of next record on the page, null if no more records exist on the page
         * @throws java.io.IOException - I/O errors
         */
        public RID nextRecord(RID curRid)
    throws java.io.IOException {
                RID rid = null;
               
                int i = curRid.slotNo + 1; //look for a non-deleted slot after the current one.
                short slotValue;
                while (i < getSlotCnt())
                {
                        slotValue = getSlotDirValue(i);
                        //Found non-deleted record.
                        if (!slotIsDeleted(slotValue))
                        {
                                rid = new RID(getCurPage(), i); //rid is <pageID, slotNo>
                                break;
                        }
                        i++;
                }
               
                return rid;
        }
       
        /**
         * copies out record with RID rid into record pointer.
         * @param rid - the record ID
         * @return - a tuple contains the record
         * @throws java.io.IOException - I/O errors
         */
        public Tuple getRecord(RID rid)
    throws java.io.IOException {
                Tuple retValue = null;
                if (0 <= rid.slotNo && rid.slotNo < getSlotCnt())
                {
                        short slotValue = getSlotDirValue(rid.slotNo);
                        if (!slotIsDeleted(slotValue))
                        {
                                short length = getSlotLength(rid.slotNo);
                                retValue = new Tuple(readRecord(slotValue, length), 0, length);
                        }
                }
                return retValue;
        }
       
        /**
         * returns a tuple in a byte array[pageSize] with given RID rid.
         * @param rid - the record ID
         * @return - a tuple with its length and offset in the byte array
         * @throws java.io.IOException - I/O errors
         */
        public Tuple returnRecord(RID rid)
    throws java.io.IOException {
                Tuple retValue = null;
                if (0 <= rid.slotNo && rid.slotNo < getSlotCnt())
                {
                        short slotValue = getSlotDirValue(rid.slotNo);
                        if (!slotIsDeleted(slotValue))
                        {
                                short length = getSlotLength(rid.slotNo);
                                retValue = new Tuple(data, slotValue, length); //point to the data array with an offset.
                        }
                }
                return retValue;
        }
       
        /**
         * Tests if a record of the given length will fit in the page.
         * @param length length of the record
         * @return true if the record will fit.
         * @throws java.io.IOException
         */
        public boolean willFit(int length)
        throws java.io.IOException
        {
                //Need space for record + slot directory entry.
                return (available_space() >= (length + this.USED_PTR));
        }
       
        /**
         * returns the amount of available space on the page.
         * @return - the amount of available space on the page.
         * @throws java.io.IOException - I/O errors
         */
        public short available_space()
    throws java.io.IOException {
                return getFreeSpace();
        }
       
        public void setDefaultFreeSpace()
        throws java.io.IOException
        {
                setFreeSpace((short)(MAX_SPACE - DPFIXED));
        }
       
        /**
         * Determining if the page is empty
         * @return - true if the HFPage is has no records in it, false otherwise
         * @throws java.io.IOException - I/O errors
         */
        public boolean empty()
    throws java.io.IOException {
                return (recordCount() == 0);
        }
       
        public int recordCount()
        throws java.io.IOException
        {
                int count = 0;
               
                if (getSlotCnt() > 0)
                {
                        int i = 0;
                        short slotValue;
                        while (i < getSlotCnt())
                        {
                                slotValue = getSlotDirValue(i);
                                //Found non-deleted record.
                                if (!slotIsDeleted(slotValue))
                                {
                                        count++;
                                }
                                i++;
                        }
                }
               
                return count;
        }
       
        /**************************************************
         * GETTERS & SETTERS
         **************************************************/
       
        /**
         *
         * @return byte array
         */
        public byte[] getHFpageArray() {
                return data;
        }
       
        /**
         *
         * @return PageId of previous page
         * @throws java.io.IOException - I/O errors
         */
        public PageId getPrevPage()
    throws java.io.IOException {
                PageId pgid = new PageId(Convert.getIntValue(PREV_PAGE, data));
                return pgid;
        }
       
        /**
         * sets value of previous page to pageNo
         * @param pageNo - page number for previous page
         * @throws java.io.IOException - I/O errors
         */
        public void setPrevPage(PageId pageNo)
    throws java.io.IOException {
                pageNo.writeToByteArray(data, PREV_PAGE);
        }
       
        /**
         *
         * @return page number of next page
         * @throws java.io.IOException - I/O errors
         */
        public PageId getNextPage()
    throws java.io.IOException {
                PageId pgid = new PageId(Convert.getIntValue(NEXT_PAGE, data));
                return pgid;
        }
       
        /**
         * sets value of nextPage to pageNo
         * @param pageNo - page number for next page
         * @throws java.io.IOException - I/O errors
         */
        public void setNextPage(PageId pageNo)
    throws java.io.IOException {
                pageNo.writeToByteArray(data, NEXT_PAGE);
        }
       
        /**
         *
         * @return page number of current page
         * @throws java.io.IOException - I/O errors
         */
        public PageId getCurPage()
    throws java.io.IOException {
                PageId pgid = new PageId(Convert.getIntValue(CUR_PAGE, data));
                return pgid;
        }
       
        /**
         * sets value of current page to pageNo
         * @param pageNo - page number for current page
         * @throws java.io.IOException - I/O errors
         */
        public void setCurPage(PageId pageNo)
    throws java.io.IOException {
                pageNo.writeToByteArray(data, CUR_PAGE);
        }
       
        /**
         *
         * @return the type
         * @throws java.io.IOException - I/O errors
         */
        public short getType()

    throws java.io.IOException {
                return Convert.getShortValue(TYPE, data);
        }
       
        /**
         * sets value of type
         * @param valtype - an arbitrary value
         * @throws java.io.IOException - I/O errors
         */
        public void setType(short valtype)
    throws java.io.IOException {
                Convert.setShortValue(valtype, TYPE, data);
        }
       
        /**
         *
         * @return slotCnt used in this page
         * @throws java.io.IOException - I/O errors
         */
        public short getSlotCnt()
    throws java.io.IOException {
                return Convert.getShortValue(SLOT_CNT, data);
        }
       
        /**
         * Sets the value at SLOT_CNT.
         * @param value
         * @throws java.io.IOException
         */
        private void setSlotCnt(short value)
        throws java.io.IOException {
                Convert.setShortValue(value, SLOT_CNT, data);
        }
       
        /**
         * Gets the free space value from the data array.
         * @return
         * @throws java.io.IOException
         */
        private short getFreeSpace()
    throws java.io.IOException {
                return Convert.getShortValue(FREE_SPACE, data);
        }
       
        /**
         * Sets the freeSpace value in the byte array.
         * @param value new value
         */
        private void setFreeSpace(short value)
        throws java.io.IOException
        {
                Convert.setShortValue(value, FREE_SPACE, data);
        }

        /**
         * gets the directory value in the requested slot.  
         * @param slotNumber
         * @return value of slot dir or -1 if invalid slot.
         * @throws java.io.IOException
         */
        private short getSlotDirValue(int slotNumber)
        throws java.io.IOException
        {
                short retValue = -1;
                if (0 <= slotNumber && slotNumber < getSlotCnt())
                        retValue = Convert.getShortValue((slotNumber * USED_PTR) + DPFIXED, data);
                return retValue;
        }
       
        /**************************************************
         * UTILITY FUNCTIONS
         **************************************************/
       
        /**
         * Dump (print) contents of a page
         * @throws java.io.IOException - I/O errors
         */
        public void dumpPage()
    throws java.io.IOException {
                System.out.println("Dumping page contents...");
                System.out.println("Slot Count: " + getSlotCnt());
                System.out.println("Free Space: " + getFreeSpace());
                System.out.println("Record Count: " + recordCount());
                System.out.println("Type: " + getType());
                System.out.println("Prev Page Id: " + getPrevPage());
                System.out.println("Next Page Id: " + getNextPage());
                System.out.println("Cur Page Id: " + getCurPage());
                System.out.println("");
               
                for (int i = 0; i < getSlotCnt(); i++)
                {
                        System.out.println("Slot #: " + i);
                        System.out.println("   Dir value: " + getSlotDirValue(i));
                        System.out.println("   Length: " + getSlotLength(i));
                        System.out.println("   Data: " + readRecord(abs(getSlotDirValue(i)), getSlotLength(i)));
                }
                System.out.println("");
        }
       
        /**
         * Writes the record to the data array.
         * @param record data to be written
         * @param location point to start writing at
         */
        private void writeRecord(byte[] record, short location)
        {
                for (int i = 0; i < record.length; i++)
                {
                        data[location + i] = record[i];
                }
        }
       
        /**
         * reads the record from the data array into a *new* byte array.
         * @param location
         * @param length
         * @return
         */
        private byte[] readRecord(short location, short length)
        {
                byte[] retValue = new byte[length];
                for (int i = 0; i < length; i++)
                {
                        retValue[i] = data[location + i];
                }
                return retValue;
        }
       
        /**
         * Gets the value of the last pointer.
         * @return
         * @throws java.io.IOException
         */
        private short getStartOfFreeSpace() throws java.io.IOException
        {
                short startFreeSpace;
               
                if (getSlotCnt() > 0)
                {
                        //take the absolute value because we don't care if the slot has been deleted or not.
                        startFreeSpace = abs(Convert.getShortValue((((getSlotCnt() - 1) * USED_PTR) + DPFIXED), data));
                }
                else
                {
                        startFreeSpace = MINIBASE_PAGESIZE;
                }
               
                return startFreeSpace;
        }
       
        /**
         * Inserts the value into the next open place in the slot directory.
         * @param value
         * @throws java.io.IOException
         */
        private void insertInSlotDirectory(short value) throws java.io.IOException
        {
                updateSlotDirectory(getSlotCnt(), value);
        }
       
        /**
         * Sets the value in the slot directory of the given slot.
         * @param slotNumber
         * @param value
         * @throws java.io.IOException
         */
        private void updateSlotDirectory(int slotNumber, short value) throws java.io.IOException
        {
                Convert.setShortValue(value, ((slotNumber * USED_PTR) + DPFIXED), data);
        }

        /**
         * returns true if the slot value is deleted (negative).
         * @param slotvalue
         * @return
         */
        private boolean slotIsDeleted(short slotvalue)
        {
                return (slotvalue < 0);
        }
       
        /**
         * Sets the slot to deleted that corresponds to the slotNumber.
         * @param slotNumber
         * @throws java.io.IOException
         */
        private void setSlotDeleted(int slotNumber) throws java.io.IOException
        {
                updateSlotDirectory(slotNumber, getDeletedSlotValue(getSlotDirValue(slotNumber)));
        }
       
        /**
         * gets the value of the slot if it is deleted (negative in this case).
         * @param slotValue
         * @return
         */
        private short getDeletedSlotValue(short slotValue)
        {
                return ((short)(abs(slotValue) * -1));
        }
       
        /**
         * Gets the absolute value of a short.
         * @param value
         * @return
         */
        private short abs(short value)
        {
                if (value < 0)
                        value *= -1;
                return value;
        }
       
        /**
         * Gets the maximum size allowed for a record.
         * @return
         */
        public static int getMaxRecordSize()
        {
                return MAX_SPACE - DPFIXED;
        }
}
