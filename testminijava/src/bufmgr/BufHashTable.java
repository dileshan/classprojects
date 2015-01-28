package bufmgr;

import global.*;
import java.util.*;

/**
 * Implements a hash table with linked lists for collisions.
 */
public class BufHashTable {

    private static int a = 3;
    private static int b = 5;
    
    private int HTSIZE;
    private BufBucket[] directory;
    
    public BufHashTable(int size)
    {
        HTSIZE = size;
        directory = new BufBucket[HTSIZE];
    }
    
    /**
     * Inserts the given PageId, FrameNumber pair.
     * @param pid PageId of the pair.
     * @param fid FrameNumber of the pair.
     */
    public void AddBucket(PageId pgid, int fid)
    {
        int location = hashPageId(pgid.pid);
        BufBucket bucket = new BufBucket(pgid.pid, fid);
        BufBucket lastBucket = GetLastBucket(location);
        
        //No bucket in that spot yet.
        if (lastBucket == null)
        {
            directory[location] = bucket;
        }
        //Collision so we add to end of list.
        else
        {
            lastBucket.setNextBucket(bucket);
        }
    }
    
    /**
     * Removes the given PageId if it exists.
     * @param pid PageId to remove.
     */
    public void RemovePage(PageId pgid)
    {
        int location = hashPageId(pgid.pid);
        BufBucket bkt = null, prevbkt = null;
        bkt = directory[location];
        
        while (bkt != null)
        {
                if (bkt.getPageNumber() == pgid.pid)
                {
                        //first in list
                        if (prevbkt == null)
                                directory[location] = bkt.getNextBucket();
                        else
                                prevbkt.setNextBucket(bkt.getNextBucket());
                        break;
                }
                prevbkt = bkt;
                bkt = bkt.getNextBucket();
        }
    }
    
    /**
     * Gets the frame for the given PageID.
     * @param pid PageId to search for.
     * @return The corresponding frame id or -1 if not found.
     */
    public int GetFrameId(PageId pgid)
    {
        BufBucket bucket = getBucket(pgid);
        if (bucket == null)
        {
            return -1;
        }
        else
        {
            return bucket.getFrameNumber();
        }
    }
    
    /**
     * Gets the bucket corresponding to the given PageId.
     * @param pid PageId to look for.
     * @return The corresponding bucket or null if not found.
     */
    private BufBucket getBucket(PageId pgid)
    {
        int location = hashPageId(pgid.pid);
        BufBucket bucket = directory[location];
        while (bucket != null && bucket.getPageNumber() != pgid.pid)
        {
            bucket = bucket.getNextBucket();
        }
        return bucket;
    }
    
    /**
     * Gets the parent bucket of the given PageId.
     * @param pid PageId to search for.
     * @return Returns parent bucket.  Return value is null if
     * bucket is not found OR bucket is first in list.
     */
    private BufBucket getBucketParent(PageId pgid)
    {
        int location = hashPageId(pgid.pid);
        BufBucket parentBucket = null;
        BufBucket bucket = directory[location];
        while (bucket != null && bucket.getPageNumber() != pgid.pid)
        {
            parentBucket = bucket;
            bucket = bucket.getNextBucket();
        }
        return parentBucket;
    }
    
    /**
     * Hash function for this hash table.
     * @param pid Page to hash.
     * @return value of hash function.
     */
    private int hashPageId(int value)
    {
        return (a * value + b) % HTSIZE;
    }
    
    /**
     * Gets the last bucket in the given location.
     * @param location the location in the array.
     * @return last bucket in the given location.
     */
    private BufBucket GetLastBucket(int location)
    {
        if (directory[location] == null)
            return null;
        
        BufBucket bucket = directory[location];
        while (bucket.getNextBucket() != null)
        {
            bucket = bucket.getNextBucket();
        }
        return bucket;
    }
    
    public ArrayList<BufBucket> GetBucketList()
    {
        ArrayList<BufBucket> buckets = new ArrayList<BufBucket>();
        BufBucket bkt;
        
        for (int i = 0; i < HTSIZE; i++)
        {
            bkt = directory[i];
            while (bkt != null)
            {
                buckets.add(bkt);
                bkt = bkt.getNextBucket();
            }
        }
        
        return buckets;
    }
    
    public void PrintHashTable()
    {
        BufBucket bkt;
        for (int i = 0; i < HTSIZE; i++)
        {
            System.out.print("\n[" + i + "]: ");
            bkt = directory[i];
            while (bkt != null)
            {
                System.out.print("Frame: " + bkt.getFrameNumber() + " Page: " + bkt.getPageNumber() + " | ");
                bkt = bkt.getNextBucket();
            }
        }
    }
 }

