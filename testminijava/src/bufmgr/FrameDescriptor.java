package bufmgr;

import global.*;

/* This class describes the page that is stored in the corresponding frame.
 * It keeps track of the aformentioned page number, the dirty bit (to see if
 * the frame has been changed), and the current pin count (to see how many things
 * are currently accessing the frame).
 */
public class FrameDescriptor {
        
        private boolean isEmpty = true; //set to true when the page_number is written to so we know it's not just a place holder.
        private boolean dirty;
        private int pin_count;
        private int page_number;
        
        /**
         * Initializes an empty frame descriptor.
         */
        public FrameDescriptor(){
                dirty = false;
                pin_count = 0;
                page_number = 0;
        }
        
        public FrameDescriptor(boolean dirt, int pins, int id){
                dirty = dirt;
                pin_count = pins;
                setID(id);
        }
        
        public void increasePinCount(){
                pin_count++;
        }
        
        public void decreasePinCount(){
                pin_count--;
        }
        
        public int getPinCount(){
                return pin_count;
        }
        
        public boolean isDirty(){
                return dirty;
        }
        
        public void setDirty(boolean dirt){
                dirty = dirt;
        }
        
        public int getID(){
                return page_number;
        }
        
        public void setID(int id){
                page_number = id;
                isEmpty = false;
        }
        
        public boolean isEmpty()
        {
            return isEmpty;
        }

}

