/*  File BufMgr,java */

package bufmgr;

import java.io.*;
import java.util.*;
import bufmgr.*;
import diskmgr.*;
import global.*;


/** The buffer manager class, it allocates new pages for the
 * buffer pool, pins and unpins the frame, frees the frame
 * page, and uses the replacement algorithm to replace the
 * page.
 */
public class BufMgr implements GlobalConst{

        private int bufferSize;                     //Initialized buffer size
        private String replacePolicy;               //Holds selected replacement policy (not used)
        private byte[][] bufferPool;                //2D byte array
        private Clock replacement;                  //Clock LRU, holds frames with 0 pin count
        private FrameDescriptor[] frameDescriptors; //Array of FrameDescriptors
        private BufHashTable directory;             //Holds all of the pages in the buffer pool
       
        /**
         * Create a buffer manager object.
         *
         * @param numbufs number of buffers in the buffer pool.
         * @param replacerArg name of the buffer replacement policy.
         */
        public BufMgr(int numbufs, String replacerArg) {
            if(numbufs < 0)
                throw new UnsupportedOperationException("numbufs needs to be a positive integer");

            bufferSize = numbufs;
            replacePolicy = replacerArg;

            if(replacePolicy == "Clock")
            {
                //
                replacement = new Clock();
                for(int i = 0; i < bufferSize; i++)
                    replacement.add(new Integer(i));
            }
            else
                throw new UnsupportedOperationException("Replacement Type not recognized.");

            bufferPool = new byte[bufferSize][GlobalConst.MINIBASE_PAGESIZE];
            frameDescriptors = new FrameDescriptor[bufferSize];
            //Initialize frameDescriptor to empty frames
            for (int i = 0; i < bufferSize; i++)
                frameDescriptors[i] = new FrameDescriptor();

          directory = new BufHashTable(23);  //Chose a prime number at random, static value???
        }


  /** Check if this page is in buffer pool, otherwise
   * find a frame for this page, read in and pin it.
   * Also write out the old page if it's dirty before reading
   * if emptyPage==TRUE, then actually no read is done to bring
   * the page in.
   *
   * @param Page_Id_in_a_DB page number in the minibase.
   * @param page the pointer poit to the page.
   * @param emptyPage true (empty page); false (non-empty page)
   */
  public void pinPage(PageId pin_pgid, Page page, boolean emptyPage)
        throws BufferPoolExceededException, Exception {
      int frameNumber = directory.GetFrameId(pin_pgid);
     
      if(frameNumber != -1)  //This means the page exists in our hashtable, which means it's
                             //already in the bufferPool.  This makes life easy.
      {
          if(replacement.find(frameNumber))    //We need to ensure this frame is removed as
                  replacement.remove(frameNumber); //a replacement candidate upon being pinned.
         
          frameDescriptors[frameNumber].increasePinCount();  //Pin it.
          page.setpage(bufferPool[frameNumber]);      //Set the page.
      }
      else     //This means the page doesn't exist in the bufferPool.
      {
          //get available frame (also removes it from the queue)
          frameNumber = replacement.pickFrame();
          if (frameNumber == -1)
          {
              throw new BufferPoolExceededException(null, "No Unpinned Frames Left");
          }
         
          //Get page id for the page currently in the frame.
          PageId oldPageId = new PageId();
          FrameDescriptor oldFrameDesc = frameDescriptors[frameNumber];
          oldPageId.pid = oldFrameDesc.getID();
         
          //write if dirty
          if(frameDescriptors[frameNumber].isDirty())
          {
              flushPage(oldPageId);
          }

          //read page.
          Page temp = new Page();
          if (!emptyPage)
          {
              try
              {
                  SystemDefs.JavabaseDB.read_page(pin_pgid, temp);
              }
              catch (Exception ex)
              {
                  throw ex;
              }
          }
         
          //remove old page,frame pair from hash table
          if (!oldFrameDesc.isEmpty())
              directory.RemovePage(oldPageId);
         
          //pin new page in frameNumber
          frameDescriptors[frameNumber] = new FrameDescriptor(false, 1, pin_pgid.pid);
          frameCopy(frameNumber, pin_pgid, temp);
         
                  //add to hash table & buffer pool
                frameDescriptors[frameNumber].increasePinCount();
                directory.AddBucket(pin_pgid, frameNumber);
                page.setpage(bufferPool[frameNumber]);
      }
  }


  /**
   * To unpin a page specified by a pageId.
   *If pincount>0, decrement it and if it becomes zero,
   * put it in a group of replacement candidates.
   * if pincount=0 before this call, return error.
   *
   * @param globalPageId_in_a_DB page number in the minibase.
   * @param dirty the dirty bit of the frame
   */
  public void unpinPage(PageId PageId_in_a_DB, boolean dirty)
        throws PageUnpinnedException, HashEntryNotFoundException {
      int frameNumber = directory.GetFrameId(PageId_in_a_DB);
     
      //Page is in buffer pool.
      if (frameNumber != -1)
      {
          //check pin count
          if (frameDescriptors[frameNumber].getPinCount() == 0)
          {
              throw new PageUnpinnedException(null, "Page has pin count of 0.");
          }
         
          //set dirty bit
          if (dirty)
          {
              frameDescriptors[frameNumber].setDirty(dirty);
          }
         
          //decrease pin count
          frameDescriptors[frameNumber].decreasePinCount();
         
          //if pin count is now 0, add to replacement candidates.
          if (frameDescriptors[frameNumber].getPinCount() == 0)
          {
              replacement.add(frameNumber);
          }
      }
      //Throw exception if page not in pool.
      else
      {
          throw new HashEntryNotFoundException(null, "Page not in buffer pool.");
      }
  }


  /** Call DB object to allocate a run of new pages and
   * find a frame in the buffer pool for the first page
   * and pin it. If buffer is full, ask DB to deallocate
   * all these pages and return error (null if error).
   *
   * @param firstpage the address of the first page.
   * @param howmany total number of allocated new pages.
   * @return the first page id of the new pages.
   */
  public PageId newPage(Page firstpage, int howmany)
        throws PageAllocationException {
      //Add first page to buffer
      PageId pid = new PageId();
      Page newPage = new Page();
      try
      {
          SystemDefs.JavabaseDB.allocate_page(pid, howmany);
          pinPage(pid, newPage, true);
      }
      catch (Exception ex)
      {
          try
          {
              SystemDefs.JavabaseDB.deallocate_page(pid, howmany);
          }
          catch (Exception ex2)
          {
              throw new PageAllocationException(ex2, "Unable to deallocate pages.");
          }
          pid = null;
      }
      firstpage.setpage(newPage.getpage());
      return pid;
  }



  /** User should call this method if she needs to delete a page.
   * this routine will call DB to deallocate the page.
   *
   * @param globalPageId the page number in the data base.
   */
  public void freePage(PageId globalPageId)
        throws PageAllocationException, PagePinnedException, HashEntryNotFoundException {
      int frameNumber = directory.GetFrameId(globalPageId);
     
      //if in pool but pinned, throw error
      if (frameNumber != -1 && frameDescriptors[frameNumber].getPinCount() > 0)
      {
          throw new PagePinnedException(null, "Unable to free page.  Page is pinned.");
      }
      //otherwise, pin page (should take care of pinned or not pinned logic, flushing, etc.)
      //do the deallocate, unpin the page, and remove it from the frameDescriptors and hash table.
      try
      {
          Page p = new Page();
          pinPage(globalPageId, p, true);
          SystemDefs.JavabaseDB.deallocate_page(globalPageId);
          unpinPage(globalPageId, false);
          //remove frame descriptor and hash entry
          frameDescriptors[directory.GetFrameId(globalPageId)] = new FrameDescriptor();
          directory.RemovePage(globalPageId);
      }
      catch (Exception ex)
      {
          throw new PageAllocationException(ex, "Error pinning page to free");
      }
  }

  /** Added to flush a particular page of the buffer pool to disk
   * @param pageid the page number in the database.
   */
  public void flushPage(PageId pageid)
  throws HashEntryNotFoundException {
      int frameNumber = directory.GetFrameId(pageid);
      if (frameNumber != -1)
      {
          try
          {
              Page p = new Page(bufferPool[frameNumber]);
              SystemDefs.JavabaseDB.write_page(pageid,p);
              frameDescriptors[frameNumber].setDirty(false);
          }
          catch(Exception e)
          {
              throw new UnsupportedOperationException("Unable to write the page.");
          }
      }
      //Throw exception if page not in pool.
      else
      {
          throw new HashEntryNotFoundException(null, "Page not in buffer pool.");
      }
  }


  /** Flushes all pages of the buffer pool to disk
   */
  public void flushAllPages() {
      ArrayList<BufBucket> buckets = directory.GetBucketList();
     
      for (BufBucket b : buckets)
      {
          try
          {
              PageId pgid = new PageId();
              pgid.pid = b.getPageNumber();
              flushPage(pgid);
          }
          catch (HashEntryNotFoundException ex)
          {
              throw new UnsupportedOperationException("Hash entry not found in buffer pool.  Something is very wrong.");
          }
      }
  }


  /** Gets the total number of buffers.
   *
   * @return total number of buffer frames.
   */
  public int getNumBuffers() {
      return bufferSize;
  }


  /** Gets the total number of unpinned buffer frames.
   *
   * @return total number of unpinned buffer frames.
   */
  public int getNumUnpinnedBuffers() {
      return (replacement.size());
  }
 
  private void frameCopy(int frame, PageId id, Page page){
          boolean dirty = false;
      int pinCount = 0;
      PageId pageId = new PageId(id.pid);
               
                FrameDescriptor fDesc = new FrameDescriptor(dirty, pinCount, pageId.pid);
                frameDescriptors[frame] = fDesc;
               
                byte[] data = page.getpage();
               
                for(int i = 0; i < GlobalConst.MINIBASE_PAGESIZE; i++)
      {
                        bufferPool[frame][i] = data[i];
      }
  }
  //prints out hash table,
  private void PrintStatusInfo(String callingMessage)
  {
          try
          {
      System.out.println("============ STATUS ===========");
      System.out.println("Calling message: " + callingMessage);
      System.out.println("Status Info:");
     
      System.out.println("\nFRAME DESCRPTOR");
      for (int i = 0; i < bufferSize; i++)
          System.out.println("PageId: " + frameDescriptors[i].getID() + "\tIsEmpty: " + frameDescriptors[i].isEmpty() + "\tPin Count: " + frameDescriptors[i].getPinCount() + "\tDirty: " + frameDescriptors[i].isDirty() + "\tIntValue: " + Convert.getIntValue(0, bufferPool[i]));
     
      System.out.println("\nREPLACEMENT");
      //replacement list, hash table
      System.out.println("Size of replacement: " + replacement.size());
     
      System.out.println("\nHASH TABLE");
      directory.PrintHashTable();
     
      System.out.println("\n===============================");
          }
          catch (Exception ex)
          {
                 
          }
  }
}

