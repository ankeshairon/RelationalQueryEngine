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
        this.KwaySort(1,2);
        reset();
    }
    
    public void KwaySort(int i, int j) {
    	
    	FileInputStream reader1 = null;
    	ObjectInputStream oos1 = null;
    	FileInputStream reader2 = null;
    	ObjectInputStream oos2 = null;
    	List<Datum[]> list1 = new ArrayList<>();
    	List<Datum[]> list2 = new ArrayList<>();
    	
    	if (i <= blockno && j <= blockno ) {
    		try { 
    			reader1 = new FileInputStream(swapDir+"/Sort"+i);
    			reader2 = new FileInputStream(swapDir+"/Sort"+j);
    			oos1 = new ObjectInputStream(reader1);
    			oos2 = new ObjectInputStream(reader2);
    	    	list1.add((Datum[]) oos1.readObject());
    	    	list2.add((Datum[]) oos2.readObject());
    	    	this.sortMerge(list1,list2);
    		} 
    		catch (FileNotFoundException e) { e.printStackTrace(); } 
    		catch (IOException e) { e.printStackTrace();} 
    		catch (ClassNotFoundException e) { e.printStackTrace(); }
    		
    	}
    	else if (i <= blockno && j > blockno) {
    		try {
    			reader1 = new FileInputStream(swapDir+"/Sort"+i);
    			oos1 = new ObjectInputStream(reader1);
    			list1.add((Datum[]) oos1.readObject());
    			this.sortMerge(list1,list2);
    		} 
    		catch (FileNotFoundException e) { e.printStackTrace(); } 
    		catch (IOException e) { e.printStackTrace(); } 
    		catch (ClassNotFoundException e) { e.printStackTrace(); }
    	}
    	else {
    			reader1 = null;
    			reader2 = null;
    	}
    	
    }
    
    public void sortMerge(List queue1, List queue2) {
    	
    	List<Datum[]> mergedQueue = new ArrayList<>();
    	mergedQueue.addAll(queue1);
    	mergedQueue.addAll(queue2);
    	Collections.sort(mergedQueue, new TupleComparator(indexesOfColumnsToSortOn));
    	
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