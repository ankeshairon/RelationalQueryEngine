package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.comparator.TupleComparator;
import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;

import java.io.*;
import java.util.*;

/*
 * Set BlockSize parameter manually 
 * If #tuples exceeds BlockSize, external sort is activated
 * Else normal sort happens.
 */
public class ExternalSort implements Operator{
	Operator input;
    private LinkedHashMap<Integer, Boolean> indexesOfColumnsToSortOn;
    private Iterator<Datum[]> tupleListIterator;
    private List<Datum[]> tupleList;
    private File swapDir;
    private int counter;
    private static int blockno;
    private static int originalBlocks;
    private static int extraBlocks = 0;
    private static int currentsize = 0;
    private static int blocksize = 1000;
    private static int originalsize;
    private static int writeBlock;
    private boolean externalSortActivated = false;
    private TupleComparator tupleComparator;
    private List<Datum[]> mergedList;
    private FileInputStream sortedOutputFile;
    private ObjectInputStream sortedDatumObjects;
    
    
    
    public ExternalSort(Operator input, LinkedHashMap<Integer, Boolean> indexesOfColumnsToSortOn, File swapDir) {
        this.input = input;
        this.indexesOfColumnsToSortOn = indexesOfColumnsToSortOn;
        this.swapDir = swapDir;
        this.tupleList = new ArrayList<>();
        this.tupleComparator = new TupleComparator(indexesOfColumnsToSortOn);
        this.mergedList = new ArrayList<>();
        this.originalsize = blocksize;
        pullAllData();
    }
    
    

    @Override
    public Datum[] readOneTuple() {
    	//If External sort
    	if (externalSortActivated) {
				return getNextDatum(sortedDatumObjects)	;
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
            if (counter >= blocksize){
                Collections.sort(tupleList, tupleComparator);
                externalSortActivated = true;
                blockno++;
                originalBlocks++;
                FileOutputStream fout;
				try {
					fout = new FileOutputStream(swapDir.getAbsolutePath()+"/Sort"+blockno);
					ObjectOutputStream oos = new ObjectOutputStream(fout);
					for (Datum[] tupleall : tupleList){
						oos.writeObject(tupleall);
					}
					oos.close();
	                tupleList = new ArrayList<>();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					counter = 0;
				}
            }
        }
        if (externalSortActivated) {
        	writeBlock = blockno + 1;
        	this.KwaySort(1, 2);
        } 
        else {
        	Collections.sort(tupleList, new TupleComparator(indexesOfColumnsToSortOn));
        	reset();
        }
    }
    
    /*
     * External Sort recursive code
     */
    
    private Datum[] getNextDatum(ObjectInputStream ois) {
    	try {
			Datum[] data = (Datum[])ois.readObject();
			return data;
		}
    	catch (EOFException e) { return null; }
    	catch (ClassNotFoundException | IOException e) { e.printStackTrace();}
    	return null;
    }
    
    public void KwaySort(int readBlock1, int readBlock2) {
    	
    	FileInputStream reader1 = null;
    	ObjectInputStream ois1 = null;
    	FileInputStream reader2 = null;
    	ObjectInputStream ois2 = null;
    	Datum[] tuple1 = null;
    	Datum[] tuple2 = null;
    	Datum[] prevTuple1 = null;
    	Datum[] prevTuple2 = null;
    	int result;
    	    	
    	if (readBlock1 < originalBlocks && readBlock2 <= originalBlocks ) {
    		try { 
    			reader1 = new FileInputStream(swapDir.getAbsolutePath()+"/Sort"+readBlock1);
    			reader2 = new FileInputStream(swapDir.getAbsolutePath()+"/Sort"+readBlock2);
    			ois1 = new ObjectInputStream(reader1);
    			ois2 = new ObjectInputStream(reader2);
    			prevTuple1 = getNextDatum(ois1);
    			prevTuple2 = getNextDatum(ois2);
    			/*
    			 * While we keep getting Datum objects from either streams
    			 */
    			while (prevTuple1 != null && prevTuple2 != null) {
    				if (tuple1 == null) {
    					tuple1 = prevTuple1;
    				}
    				if (tuple2 == null) {
    					tuple2 = prevTuple2;    	
    				}
    				if (tuple1 != null && tuple2 != null) {
    					result = tupleComparator.compare(tuple1, tuple2);
    					if (result == 0 || result > 0) {
    						this.sortMerge(tuple2);
    						tuple2 = null;
    						prevTuple2 = getNextDatum(ois2);
    					}
    					else if (result < 0) {
    						this.sortMerge(tuple1);
    						tuple1 = null;
    						prevTuple1 = getNextDatum(ois1);
    					}
    				}
    			}
    			/*
    			 * If we have tuples from only one stream
    			 */
    			if (prevTuple1 != null) {
    				do {
    					this.sortMerge(prevTuple1);
    					prevTuple1 = getNextDatum(ois1);
    				} while (prevTuple1 != null);
    			}
    			else if (prevTuple2 != null) {
    				do {
    					this.sortMerge(prevTuple2);
    					prevTuple2 = getNextDatum(ois2);
    				} while (prevTuple2 != null);
    			}
    			ois1.close();
    			ois2.close();
    			reader1.close();
    			reader2.close();
    		}
    		catch (IOException e) { e.printStackTrace();}
    		this.sortMerge(null);
    		KwaySort(readBlock2+1,readBlock2+2);
    		return;
    	}
    
    	else if (readBlock1 == originalBlocks && readBlock2 > originalBlocks && blockno != 1) {
    		try {
    			reader1 = new FileInputStream(swapDir.getAbsolutePath()+"/Sort"+readBlock1);
    			ois1 = new ObjectInputStream(reader1);
    			Datum[] data = getNextDatum(ois1);
    			while (data != null) {
    				this.sortMerge(data);
    				data = getNextDatum(ois1);
    			}
    			this.sortMerge(null);
				ois1.close();
				reader1.close();
				//Pass Ends
    			blockno = blockno / 2;
    			blocksize = blocksize * 2;
    			int startBlock = originalBlocks;
    			originalBlocks = originalBlocks + extraBlocks;
    			extraBlocks = 0;
    			KwaySort(startBlock + 1, startBlock + 2);
    		} 
    		catch (FileNotFoundException e) { e.printStackTrace(); } 
    		catch (IOException e) { e.printStackTrace(); }
    		return;
    	}
    	else {
    			if (blockno == 1) {
    				//Sort Ends
    				writeBlock--;
    				setIterator();
    			}
    			else {
    				//Pass Ends
    				blockno = blockno / 2;
    				blocksize = blocksize * 2;
        			int startBlock = originalBlocks;
        			originalBlocks = originalBlocks + extraBlocks;
        			extraBlocks = 0;
        			KwaySort(startBlock + 1, startBlock + 2);    			
        		}
    	}
    	
    }
    
    private ObjectOutputStream getObjectStream(String fileName) {
    	try {
    		File file = new File(fileName);
    		if(file.exists()) {
    			return new AppendingObjectOutputStream(new FileOutputStream(fileName,true));
    		}
    		else {
    			return new ObjectOutputStream(new FileOutputStream(fileName));
    		}
    	}
		catch (FileNotFoundException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
    	return null;
    }
    
    public void sortMerge(Datum[] data) {

    	ObjectOutputStream oos;
    	
    	if (data == null || currentsize >= originalsize) {
    		try {
   				oos = getObjectStream(swapDir.getAbsolutePath()+"/Sort"+writeBlock);
   				for (Datum[] tupleAll: mergedList){
   					oos.writeObject(tupleAll);
   				}
   				oos.flush();
   				oos.close();
   				mergedList = new ArrayList<>();
    		} 
    		catch (FileNotFoundException e) { e.printStackTrace(); }
    		catch (IOException e) { e.printStackTrace(); }
    		currentsize = 0;
    		if (data == null) {
    			writeBlock++;
    			extraBlocks++;
    		}
    	}
    	else {
    		currentsize++;
        	mergedList.add(data);
    	}
    }
    
    private void setIterator() {
        try {
			sortedOutputFile = new FileInputStream(swapDir.getAbsolutePath() + "/Sort" + writeBlock);
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

    @Override
    public Long getProbableTableSize() {
        throw new UnsupportedOperationException("Unable to get size of data in External sort");
    }
}
/*
 * Java hack to remove ObjectStream Header
 */

class AppendingObjectOutputStream extends ObjectOutputStream {

	  public AppendingObjectOutputStream(OutputStream out) throws IOException {
		  super(out);
	  }

	  @Override
	  protected void writeStreamHeader() throws IOException {
	    // do not write a header, but reset:
		  reset();
	  }

}