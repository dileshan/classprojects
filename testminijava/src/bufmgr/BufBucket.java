package bufmgr;

import global.*;

public class BufBucket {

    private int page_number;
    private int frame_number;
    private BufBucket next_bucket;
    
    public BufBucket(int pid, int fid)
    {
        page_number = pid;
        frame_number = fid;
    }
    
    public void setPageNumber(int pid)
    {
        page_number = pid;
    }
    
    public int getPageNumber()
    {
        return page_number;
    }
    
    public void setFrameNumber(int fid)
    {
        frame_number = fid;
    }
    
    public int getFrameNumber()
    {
        return frame_number;
    }
    
    public void setNextBucket(BufBucket nextBkt)
    {
        next_bucket = nextBkt;
    }
    
    public BufBucket getNextBucket()
    {
        return next_bucket;
    }
}

