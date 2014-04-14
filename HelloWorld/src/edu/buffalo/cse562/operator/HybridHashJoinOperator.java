package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.data.*;
import edu.buffalo.cse562.data.Datum.CastException;
import edu.buffalo.cse562.schema.ColumnSchema;

import java.io.*;
import java.util.*;

public class HybridHashJoinOperator implements Operator {

    Operator R,S;
    ColumnSchema[] schemaR,schemaS,outputSchema;
    int indexR,indexS;	
    File swapDir;
    int hashSize,hashSize2;
    ArrayList<Datum[]> result;
    Iterator<Datum[]> iter;
    
    File outputFile;
    BufferedReader output;
    
    ArrayList<BufferedWriter> bwR;
    ArrayList<BufferedWriter> bwS;
    
    ArrayList<BufferedReader> brR;
    ArrayList<BufferedReader> brS;

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public HybridHashJoinOperator(Operator R, Operator S, int indexR, int indexS, File swapDir) throws CastException, IOException{

        this.R = R; this.S = S;
        this.indexR = indexR; this.indexS = indexS;
        schemaR = R.getSchema(); schemaS = S.getSchema();
        this.swapDir = swapDir;
        result = new ArrayList<Datum[]>();
        
        // Change these values to fine tune hash-function
        hashSize = 1499;
        hashSize2 = 401;
        
        bwR = new ArrayList<BufferedWriter>(hashSize);
        bwS = new ArrayList<BufferedWriter>(hashSize);
        
        brR = new ArrayList<BufferedReader>(hashSize);
        brS = new ArrayList<BufferedReader>(hashSize);
        
        createTempFiles(bwR,"R");
        createTempFiles(bwS,"S");
        
        partitionData(R,indexR,bwR);
        partitionData(S,indexS,bwS);
        
        closeWriters(bwR);
        closeWriters(bwS);
        
        createReaders(brR,"R");
        createReaders(brS,"S");
        
        outputFile = File.createTempFile("Result", "tmp", swapDir);
        
        join();
        
        closeReaders(brR);
        closeReaders(brS);
        
        cleanup();
        
        updateSchema();
        
        reset();
        //iter = result.iterator();
    }
    
    public void closeReaders(ArrayList<BufferedReader> br) throws IOException{
    	for(int i=0;i<hashSize;i++){
    		BufferedReader temp = br.get(i);
    		temp.close();
    	}
    }
    
    public void join() throws IOException{
    	for(int i=0;i<hashSize;i++){
    		BufferedReader br = brR.get(i);
    		HashMap<Integer,ArrayList<Datum[]>> build = new HashMap<Integer,ArrayList<Datum[]>>(hashSize2);
    		String line;
    		// secondary hash build phase
    		while((line = br.readLine())!=null){
    			Datum[] tuple = parseLine(line,schemaR);
    			int hashKey = hashFunction(tuple[indexR], hashSize2);
    			ArrayList<Datum[]> list = build.get(hashKey);
    			if(list == null){
    				list = new ArrayList<Datum[]>();
    			}
    			list.add(tuple);
    			build.put(hashKey, list);
    		}
    		
    		BufferedWriter resultWriter = new BufferedWriter(new FileWriter(outputFile, true));
    		Datum[] outTuple = new Datum[schemaR.length + schemaS.length];
    		
    		
    		// probe phase
    		br = brS.get(i);
    		while((line = br.readLine())!=null){
    			Datum[] tupleS = parseLine(line,schemaS);
    			int hashKey = hashFunction(tupleS[indexS], hashSize2);
    			ArrayList<Datum[]> list = build.get(hashKey);
    			if(list == null){
    				continue;
    			}
    			// perform join
    			for(Datum[] tupleR : list){
    				StringBuilder tempLine = new StringBuilder();
    				if(tupleR[indexR].toSTRING().equals(tupleS[indexS].toSTRING())){
    					int counter = 0;
    					for(int j=0;j<tupleR.length;j++){
    						tempLine.append(tupleR[j].toSTRING()+"|");
    						//outTuple[counter] = tupleR[j];
    						counter++;
    					}
    					for(int j=0;j<tupleS.length;j++){
    						if(j==tupleS.length-1){
    							tempLine.append(tupleS[j].toSTRING());
    						}else{
    							tempLine.append(tupleS[j].toSTRING()+"|");
    						}
    						//outTuple[counter] = tupleS[j];
    						counter++;
    					}
    					resultWriter.write(tempLine.toString());
    					resultWriter.newLine();
    					//result.add(outTuple);
    				}
    			}
    		}
    		resultWriter.close();
    	}
    }
    
    public void createReaders(ArrayList<BufferedReader> br, String prefix){
    	try {
			for(int i=0;i<hashSize;i++){
				BufferedReader temp = new BufferedReader(new FileReader(swapDir.getAbsolutePath()+"/"+prefix+i));
				br.add(temp);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }
    
    public void createTempFiles(ArrayList<BufferedWriter> bw, String prefix){
    	for(int i=0;i<hashSize;i++){
    		try {
				BufferedWriter temp = new BufferedWriter(new FileWriter(swapDir.getAbsolutePath()+"/"+prefix+i,true));
				bw.add(temp);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
    
    public void partitionData(Operator Op, int index, ArrayList<BufferedWriter> bw){
    	Datum[] tuple;
    	
    	while((tuple = Op.readOneTuple())!=null){
    		int hashKey = hashFunction(tuple[index], hashSize);
    		BufferedWriter temp = bw.get(hashKey);
    		String record = makeRecord(tuple);
    		try {
				temp.write(record);
				temp.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public String makeRecord(Datum[] tuple){
    	int size = tuple.length;
    	StringBuilder bld = new StringBuilder();
    	for(int i=0;i<size;i++){
    		if(i == size-1){
    			bld.append(tuple[i].toSTRING());
    		}else{
    			bld.append(tuple[i].toSTRING()+"|");
    		}
    	}
    	return bld.toString();
    	
    }
    
    public int hashFunction(Datum value, int size){
    	try {
			if(value.getType() == Datum.type.LONG){
				return value.toLONG().intValue() % size;
			}
			else if(value.getType() == Datum.type.FLOAT){
				return value.toFLOAT().intValue() % size;
			}
			else if(value.getType() == Datum.type.STRING || 
					value.getType() == Datum.type.DATE){
				return value.toSTRING().toString().length() % size;
			}
		} catch (CastException e) {
			e.printStackTrace();
		}
    	
    	return 0;
    }
    
    public void closeWriters(ArrayList<BufferedWriter> bw){
    	for(int i=0;i<bw.size();i++){
    		BufferedWriter temp = bw.get(i);
    		try {
				temp.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public void updateSchema(){
        outputSchema = new ColumnSchema[R.getSchema().length + S.getSchema().length];
        int i = 0;
        for(ColumnSchema schema : R.getSchema()){
            outputSchema[i] = schema;
            i++;
        }
        for(ColumnSchema schema : S.getSchema()){
            outputSchema[i] = schema;
            i++;
        }
    }


    public Datum[] parseLine(String line, ColumnSchema[] schema){
        Datum[] ret = new Datum[schema.length];
        String[] cols = line.split("\\|");
        for(int i=0;i<ret.length;i++){
            switch (schema[i].getType()) {
                case LONG:
                    ret[i] = new LONG(cols[i]);
                    break;
                case FLOAT:
                    ret[i] = new FLOAT(cols[i]);
                    break;
                case BOOL:
                    ret[i] = new BOOL(cols[i]);
                    break;
                case DATE:
//                try {
//                    ret[i] = new DATE(cols[i]);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                break;
                case STRING:
                    ret[i] = new STRING(cols[i]);
                    break;
                default:
                    ret[i] = new STRING(cols[i]);
                    break;
            }
        }
        return ret;
    }

    public void cleanup(){
        for(int i=0;i<hashSize;i++){
        	File file = new File(swapDir.getAbsolutePath()+"/R"+i);
        	file.delete();
        	file = new File(swapDir.getAbsolutePath()+"/S"+i);
        	file.delete();
        }
    }

    @Override
    public Datum[] readOneTuple() {
        //if(iter.hasNext()){
        //    return (Datum[])iter.next();
        //}
    	
    	if(output == null){
    		return null;
    	}
    	String line = null;
    	try {
			line = output.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	if(line == null){
    		return null;
    	}
    	Datum[] tuple = parseLine(line,outputSchema);
        return tuple;
    }

    @Override
    public void reset() {
    	
    	try {
			output = new BufferedReader(new FileReader(outputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	
    	//iter = result.iterator();
        //throw new UnsupportedOperationException("Reset not supported for " + this.getClass().getName());
    }

    @Override
    public ColumnSchema[] getSchema() {
        return outputSchema;
    }

    @Override
    public Long getProbableTableSize() {
        return R.getProbableTableSize() * S.getProbableTableSize();
    }

}