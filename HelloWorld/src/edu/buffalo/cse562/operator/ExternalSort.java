package edu.buffalo.cse562.operator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.PriorityQueue;

import edu.buffalo.cse562.comparator.TupleComparator;
import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;

public class ExternalSort implements Operator{
	Operator input;
    private LinkedHashMap<Integer, Boolean> indexesOfColumnsToSortOn;
    private Iterator<Datum[]> tupleListIterator;
    private List<Datum[]> tupleList;
    private File swapDir;
    private int counter;
    private static int blockno;
    private static int currentsize = 1000;
    private static int blocksize = 1000;
    private boolean externalSortActivated = false;
    private TupleComparator tupleComparator;
    private List<Datum[]> mergedList;
    private FileInputStream sortedOutputFile;
    private ObjectInputStream sortedDatumObjects;
    
    public ExternalSort(Operator input, LinkedHashMap<Integer, Boolean> indexesOfColumnsToSortOn, File swapDir) {
        this.input = input;
        this.indexesOfColumnsToSortOn = indexesOfColumnsToSortOn;
        this.swapDir = swapDir;
        tupleList = new ArrayList<>();
        this.tupleComparator = new TupleComparator(indexesOfColumnsToSortOn);
        this.mergedList = new ArrayList<>();
        pullAllData();
    }

    @Override
    public Datum[] readOneTuple() {
    	//If External sort
    	if (externalSortActivated) {
				try {
					return (Datum[]) sortedDatumObjects.readObject();
				} 
				catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
				return null;
    	}
    	//No external sort
    	else {
    		if (tupleListIterator.hasNext()) {
    			return tupleListIterator.next();
    		}
    		return null;
    	}
    }

    private void pullAllData() {
        Datum tuple[];
        while ((tuple = input.readOneTuple()) != null) {
            tupleList.add(tuple);
            counter++;
            if (counter > blocksize){
                Collections.sort(tupleList, tupleComparator);
                externalSortActivated = true;
                counter=0;
                blockno++;
                //Need to clear TupleList of existing values
                //tupleList=null;
                FileOutputStream fout;
				try {
					fout = new FileOutputStream(swapDir.getAbsolutePath()+"/Sort"+blockno);
					ObjectOutputStream oos = new ObjectOutputStream(fout);
	                oos.writeObject(tupleList);
	                oos.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
        if (externalSortActivated) {
        	this.KwaySort(1, 2, 1);
        } 
        else {
        	Collections.sort(tupleList, new TupleComparator(indexesOfColumnsToSortOn));
        	reset();
        }
    }
    
    
    
    public void KwaySort(int readBlock1, int readBlock2, int writeBlock) {
    	
    	FileInputStream reader1 = null;
    	ObjectInputStream ois1 = null;
    	FileInputStream reader2 = null;
    	ObjectInputStream ois2 = null;
    	Datum[] tuple1 = null;
    	Datum[] tuple2 = null;
    	Datum[] prevTuple1 = null;
    	Datum[] prevTuple2 = null;
    	int result;
    	
    	if (readBlock1 <= blockno && readBlock2 <= blockno ) {
    		try { 
    			reader1 = new FileInputStream(swapDir.getAbsolutePath()+"/Sort"+readBlock1);
    			reader2 = new FileInputStream(swapDir.getAbsolutePath()+"/Sort"+readBlock2);
    			ois1 = new ObjectInputStream(reader1);
    			ois2 = new ObjectInputStream(reader2);
    			/*
    			 * While we keep getting Datum objects from either streams
    			 */
    			while (ois1 != null && ois2 != null) {
    				/*
    				 * prevTuple is for keeping last tuple which failed the sort test 
    				 */
    				if (prevTuple1 == null) {
    					tuple1 = (Datum[]) ois1.readObject();
    				}
    				else {
    					tuple1 = prevTuple1;
    				}
    				if (prevTuple2 == null) {
    					tuple2 = (Datum[]) ois2.readObject();
    				}
    				else {
    					tuple2 = prevTuple2;
    				}
    				/*
    				 * If we have tuples from both streams compare them
    				 */
    				if (tuple1 != null && tuple2 != null) {
    					result = tupleComparator.compare(tuple1, tuple2);
    					if (result == 0 || result > 0) {
    						this.sortMerge(tuple1, writeBlock);
    						prevTuple1 = null;
    						prevTuple2 = tuple2;
    					}
    					else if (result < 0) {
    						this.sortMerge(tuple2, writeBlock);
    						prevTuple2 = null;
    						prevTuple1 = tuple1;
    					}
    				}
    				/*
    				 * If we have tuple from only one stream 
    				 */
    				else if (tuple1 != null && tuple2 == null) {
    					this.sortMerge(tuple1, writeBlock);
    				}
    				else if (tuple1 == null && tuple2 != null) {
    					this.sortMerge(tuple2, writeBlock);
    				}
    			}
    			KwaySort(readBlock2+1,readBlock2+2,readBlock2/2);
    		} 
    		catch (FileNotFoundException e) { e.printStackTrace(); } 
    		catch (IOException e) { e.printStackTrace(); } 
    		catch (ClassNotFoundException e) { e.printStackTrace(); }
    		
    	}
    	else if (readBlock1 <= blockno && readBlock2 > blockno) {
    		try {
    			reader1 = new FileInputStream(swapDir.getAbsolutePath()+"/Sort"+readBlock1);
    			ois1 = new ObjectInputStream(reader1);
    			while (ois1 != null) {
    				this.sortMerge((Datum[]) ois1.readObject(), writeBlock);
    			}
    			blockno = blockno / 2;
    			KwaySort(1,2,1);
    		} 
    		catch (FileNotFoundException e) { e.printStackTrace(); } 
    		catch (IOException e) { e.printStackTrace(); } 
    		catch (ClassNotFoundException e) { e.printStackTrace(); }
    	}
    	else {
    			if (blockno == 1) {
    				//Sort Ends
    				setIterator();
    			}
    			else {
    				blockno = blockno / 2;
    				KwaySort(1,2,1);
    			}
    	}
    	
    }
    
    public void sortMerge(Datum[] data, int writeBlock) {
    	
    	mergedList.add(data);
    	currentsize++;
    	if (blocksize == currentsize * 2) {
    		try {
    			blocksize = currentsize * 2;
    			currentsize = 0;
    			FileOutputStream out = new FileOutputStream(swapDir.getAbsolutePath()+"/Sort"+writeBlock);
    			ObjectOutputStream oos = new ObjectOutputStream(out);
    			oos.writeObject(mergedList);
    			oos.close();
    			mergedList = new ArrayList<>();
    		} 
    		catch (FileNotFoundException e) { e.printStackTrace(); }
    		catch (IOException e) { e.printStackTrace(); }
    	}
    }
    
    private void setIterator() {
        try {
			sortedOutputFile = new FileInputStream(swapDir.getAbsolutePath() + "/Sort1");
			sortedDatumObjects = new ObjectInputStream(sortedOutputFile);
		} 
        catch (FileNotFoundException e) { e.printStackTrace(); } 
        catch (IOException e) { e.printStackTrace(); }
    }

    
    @Override
    public void reset() {
        tupleListIterator = tupleList.iterator();
    }

    @Override
    public ColumnSchema[] getSchema() {
        return null;
    }
}