package edu.buffalo.cse562.operator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import edu.buffalo.cse562.comparator.TupleComparator;
import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;

public class ExternalSort implements Operator{
	Operator input;
    private LinkedHashMap<Integer, Boolean> indexesOfColumnsToSortOn;
    private Iterator<Datum[]> tupleListIterator;
    private List<Datum[]> tupleList;
    private int counter;
    private int blockno;

    public ExternalSort(Operator input, LinkedHashMap<Integer, Boolean> indexesOfColumnsToSortOn) {
        this.input = input;
        this.indexesOfColumnsToSortOn = indexesOfColumnsToSortOn;
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
					fout = new FileOutputStream("Sort"+blockno);
					ObjectOutputStream oos = new ObjectOutputStream(fout);
	                oos.writeObject(tupleList);
	                oos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                
            }
        }
        
        //Collections.sort(tupleList, new TupleComparator(indexesOfColumnsToSortOn));
        reset();
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