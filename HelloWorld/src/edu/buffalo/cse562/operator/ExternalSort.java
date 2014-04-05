package edu.buffalo.cse562.operator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    private String swapDir;
    private int counter;
    private static int blockno;
    private boolean ongoingPass = true;

    public ExternalSort(Operator input, LinkedHashMap<Integer, Boolean> indexesOfColumnsToSortOn, String swapDir) {
        this.input = input;
        this.indexesOfColumnsToSortOn = indexesOfColumnsToSortOn;
        this.swapDir = swapDir;
        tupleList = new ArrayList<>();
        pullAllData();
        reset();
    }

    @Override
    public Datum[] readOneTuple() {
        if (tupleListIterator.hasNext()) {
            return tupleListIterator.next();
        }
        return null;
    }

    private void pullAllData() {
        Datum tuple[];
        while ((tuple = input.readOneTuple()) != null) {
            tupleList.add(tuple);
            counter++;
            if (counter==1000){
                Collections.sort(tupleList, new TupleComparator(indexesOfColumnsToSortOn));
                counter=0;
                blockno++;
                //Need to clear TupleList of existing values
                //tupleList=null;
                FileOutputStream fout;
				try {
					fout = new FileOutputStream(swapDir+"/Sort"+blockno);
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
        
        //Collections.sort(tupleList, new TupleComparator(indexesOfColumnsToSortOn));
        this.KwaySort(1,2,1);
        reset();
    }
    
    public void KwaySort(int block1, int block2, int newBlock) {
    	
    	FileInputStream reader1 = null;
    	ObjectInputStream ois1 = null;
    	FileInputStream reader2 = null;
    	ObjectInputStream ois2 = null;
    	List<Datum[]> list1 = new ArrayList<>();
    	List<Datum[]> list2 = new ArrayList<>();
    	
    	if (block1 <= blockno && block2 <= blockno ) {
    		try { 
    			reader1 = new FileInputStream(swapDir+"/Sort"+block1);
    			reader2 = new FileInputStream(swapDir+"/Sort"+block2);
    			ois1 = new ObjectInputStream(reader1);
    			ois2 = new ObjectInputStream(reader2);
    	    	list1.add((Datum[]) ois1.readObject());
    	    	list2.add((Datum[]) ois2.readObject());
    	    	this.sortMerge(list1,list2,newBlock);
    	    	KwaySort(block2+1,block2+2,block2/2);
    		} 
    		catch (FileNotFoundException e) { e.printStackTrace(); } 
    		catch (IOException e) { e.printStackTrace();} 
    		catch (ClassNotFoundException e) { e.printStackTrace(); }
    		
    	}
    	else if (block2 <= blockno && block2 > blockno) {
    		try {
    			reader1 = new FileInputStream(swapDir+"/Sort"+block1);
    			ois1 = new ObjectInputStream(reader1);
    			list1.add((Datum[]) ois1.readObject());
    			this.sortMerge(list1,list2,newBlock);
    			blockno = blockno / 2;
    			KwaySort(1,2,1);
    		} 
    		catch (FileNotFoundException e) { e.printStackTrace(); } 
    		catch (IOException e) { e.printStackTrace(); } 
    		catch (ClassNotFoundException e) { e.printStackTrace(); }
    	}
    	else {
    			if (blockno == 1) {
    				//Sort ends
    			}
    			else {
    				blockno = blockno / 2;
    				KwaySort(1,2,1);
    			}
    	}
    	
    }
    
    public void sortMerge(List list1, List list2, int newBlock) {
    	
    	List<Datum[]> mergedList = new ArrayList<>();
    	mergedList.addAll(list1);
    	if (list2 != null) {
    		mergedList.addAll(list2);
    	}
    	Collections.sort(mergedList, new TupleComparator(indexesOfColumnsToSortOn));
    	try {
			FileOutputStream out = new FileOutputStream(swapDir+"/Sort"+newBlock);
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(mergedList);
			oos.close();
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