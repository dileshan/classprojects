package heap;

import java.io.*;
import java.util.*;
import java.lang.*;
import heap.*;
import bufmgr.*;
import diskmgr.*;
import global.*;
import chainexception.*;

/**
 * This class manages a tuple of fixed length (at least that's what I gather from the assignment).
*/
public class Tuple implements GlobalConst{

        //constant values from http://www-users.itlabs.umn.edu/classes/Spring-2008/csci5708/index.php?page=.//labs/HeapFile/javadoc/constant-values
        public static final int max_size = 1024;
        
        //tuple info
        private byte[] tupleData;//The data stored in the record.
        private int tupleOffset; //not sure what this is for?
        private int tupleLength;
        
        //header fields
        private short numFields;
        private AttrType[] atrTypes;
        private short[] fldOffset;
        private short[] strSizes;
        
        
        public Tuple(){
                tupleData = new byte[max_size];
                tupleOffset = 0;
                tupleLength = max_size;
        }
        
        public Tuple(byte[] atuple,
            int offset,
            int length) {
                this.tupleInit(atuple, offset, length);
        }
        
        public Tuple(Tuple fromTuple) throws InvalidUpdateException {
                this.tupleCopy(fromTuple);
        }
        
        /**
         * Class constructor Create a new tuple with length = size,tuple offset = 0. 
         * 
         * @param size
         */
        public Tuple(int size) {
                tupleData = new byte[size];
                tupleOffset = 0;
                tupleLength = size;
        }
        
        /**
         * Copy a tuple to the current tuple position you must make sure the tuple lengths must be equal 
         * 
         * @param fromTuple the tuple being copied
         */
        public void tupleCopy(Tuple fromTuple) 
        throws InvalidUpdateException {
                if (tupleLength != fromTuple.tupleLength)
                        throw new InvalidUpdateException(null, "Tuple lengths not equal");
                byte[] newData = fromTuple.getTupleByteArray();

                for (int i = 0; i < tupleLength; i++)
                {
                        tupleData[i + tupleOffset] = newData[i];
                }
        }
        
        /**
         * This is used when you don't want to use the constructor 
         * 
         * @param atuple a byte array which contains the tuple
         * @param offset the offset of the tuple in the byte array
         * @param length the length of the tuple
         */
        public void tupleInit(byte[] atuple,
            int offset,
            int length) {
                tupleData = atuple;
                tupleOffset = offset;
                tupleLength = length;
        }
        
        /**
         * Set a tuple with the given tuple length and offset 
         * 
         * @param record a byte array contains the tuple
         * @param offset the offset of the tuple ( =0 by default)
         * @param length the length of the tuple
         */
        public void tupleSet(byte[] record,
            int offset,
            int length) {
                tupleData = record.clone();
                tupleOffset = offset;
                tupleLength = length;
        }
        
        /**
         * get the length of a tuple, call this method if you did not call setHdr () before 
         * @return length of this tuple in bytes
         */
        public int getLength() {
                return tupleLength; //Here the tupleData array already contains the header.
        }
        
        /**
         * get the length of a tuple, call this method if you did call setHdr () before
         * @return size of this tuple in bytes
         */
        public short size() {
                short temp = (short)this.getLength();
                return temp;
        }
        
        public int getOffset() {
                return tupleOffset;
        }
        
        /**
         * Copy the tuple byte array out.
         * @return byte[], a byte array contains the tuple the length of byte[] = length of the tuple
         */
        public byte[] getTupleByteArray() {
                //need to copy out just the tuple part of the data array (if there's an offset).
                byte[] returnArray = new byte[tupleLength];
                
                for (int i = 0; i < tupleLength; i++)
                {
                        returnArray[i] = tupleData[i + tupleOffset];
                }
                
                return returnArray;
        }
        
        /**
         * return the data byte array
         * @return data byte array
         */
        public byte[] returnTupleByteArray() {
                return tupleData;
        }
        
        //Not tested
        public int getIntFld(int fldNo)
    throws java.io.IOException {
                return Convert.getIntValue(fldOffset[fldNo - 1], tupleData);
        }
        
        //Not tested
        public float getFloFld(int fldNo)
    throws java.io.IOException {
                return Convert.getFloValue(fldOffset[fldNo - 1], tupleData);
        }
        
        //Not tested
        public java.lang.String getStrFld(int fldNo)
    throws java.io.IOException {
                throw new UnsupportedOperationException("Not implemented");
                //return Convert.getStrValue(fldOffset[fldNo - 1], tupleData, ???);
        }
        
        //Not tested
        public char getCharFld(int fldNo)
    throws java.io.IOException {
                return Convert.getCharValue(fldOffset[fldNo - 1], tupleData);
        }
        
        //Not tested
        public Tuple setIntFld(int fldNo,
            int val)
     throws java.io.IOException {
                Convert.setIntValue(val, fldOffset[fldNo - 1], tupleData);
                return this;
        }
        
        //Not tested
        public Tuple setFloFld(int fldNo,
            float val)
     throws java.io.IOException {
                Convert.setFloValue(val, fldOffset[fldNo - 1], tupleData);
                return this;
        }
        
        //Not tested
        public Tuple setStrFld(int fldNo,
            java.lang.String val)
     throws java.io.IOException {
                Convert.setStrValue(val, fldOffset[fldNo - 1], tupleData);
                return this;
        }
        
        /**
         * setHdr will set the header of this tuple.
         * @param numFlds number of fields
         * @param types contains the types that will be in this tuple
         * @param strSizes contains the sizes of the string
         * @throws java.io.IOException
         */
        //Not tested
        public void setHdr(short numFlds,
            AttrType[] types,
            short[] strSizes)
     throws java.io.IOException {
                numFields = numFlds;
                atrTypes = types;
                this.strSizes = strSizes;
                
                //TODO: make the header in bytes?
                makeHeader(numFlds, types, strSizes);
        }
        
        private byte[] makeHeader(short numFlds, AttrType[] types, short[] strSizes) 
                throws IOException{
                //2 for numFields + 4*# types + 2*numFlds for offsets
                int headerSize = 2 + (4 * numFlds) + (2 * numFlds);
                byte[] header = new byte[headerSize];
                short offsetValue;
                
                try
                {
                        Convert.setShortValue(numFlds, 0, header); //write numFields
                        for (int i = 0; i < numFlds; i++)
                        {
                                Convert.setIntValue(types[i].attrType, (2 + (i * 4)), header);
                                
                                //determine offset value based on the AttrType
                                switch(types[i].attrType)
                                {
                                case AttrType.attrInteger:
                                case AttrType.attrReal:
                                        offsetValue = 4;
                                        break;
                                case AttrType.attrString:
                                        //TODO: See if strSizes is # of characters or # of bytes
                                        //Right now, assumes strSize is # of chars.
                                        offsetValue = 2; //this is to avoid casting 2 to a short.
                                        offsetValue *= strSizes[i];
                                        break;
                                default:
                                        throw new IOException("Unsupported AttrType.");
                                }
                                offsetValue += headerSize;
                                
                                Convert.setShortValue(offsetValue, (2 + (4 * numFlds) + (i * 2)), header);
                        }
                }
                catch (IOException ex)
                {
                        throw ex;
                }
                
                return header;
        }
        
        /**
         * Returns number of fields in this tuple
         * @return the number of fields in this tuple
         */
        public short noOfFlds() {
                return numFields;
        }
        /**
         * Makes a copy of the fldOffset array
         * @return a copy of the fldOffset array
         */
        public short[] copyFldOffset() {
                return fldOffset.clone();
        }
        
        /**
         * Prints the tuple info.  Ignores type and just prints out byte array.
         * @param type
         * @throws java.io.IOException
         */
        public void print(AttrType[] type)
    throws java.io.IOException {
                System.out.println();
                System.out.println("Printing Tuple Data");
                System.out.println("   Length: " + tupleLength);
                System.out.println("   Offset: " + tupleOffset);
                System.out.println("   Data: " + tupleData);
                System.out.println();
        }
}
