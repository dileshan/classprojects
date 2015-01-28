package bufmgr;

import java.util.*;

/* We use the Clock replacement policy to select which frame
 * we will place into the buffer next (First In, First Out).  
 * This is a simple extension of the LinkedList class with a
 * few modified functions to suit our needs.
 */
public class Clock {

        // Here's our beautiful linked list in which we hold our frames with pincount = 0.
        private LinkedList<Integer> framelist;
        
        //Here's our Constructor which instantiates our LinkedList.
        public Clock(){
                framelist = new LinkedList<Integer>();
        }
        
        /*Here's the meat of the replacer, this function pops the first frame off
         * of the front of our list and returns it so the corresponding frame can
         * be placed into the cache.
         */
        public int pickFrame() {
            if (framelist.isEmpty())
            {
                return -1;
            }
            return ((framelist).removeFirst()).intValue();
        }
        
        //Adds a frame to our list
        public void add(int frame){
                framelist.add(new Integer(frame));
        }
        
        //Removes a frame from our list
        public void remove(int frame){
                for(int i = 0; i < framelist.size(); i++){
                        if((framelist.get(i)).intValue() == frame)
                                framelist.remove(i);
                }
        }
        
        //Checks to see if a frame exists in our list
        public boolean find(int frame){
                for(int i = 0; i < framelist.size(); i++){
                        if((framelist.get(i)).intValue() == frame)
                                return true;
                }
                return false;
        }
        
        //Checks to see if our list is empty
        public boolean empty(){
                return framelist.size() == 0;
        }
        
        /**
         * Returns the size of the list.
         * @return
         */
        public int size()
        {
            return framelist.size();
        }
}

