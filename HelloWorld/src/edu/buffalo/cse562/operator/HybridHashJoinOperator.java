package edu.buffalo.cse562.operator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.data.Datum.CastingException;
import edu.buffalo.cse562.data.Datum.LONG;
import edu.buffalo.cse562.schema.ColumnSchema;

public class HybridHashJoinOperator implements Operator {
	
	Operator R,S;
	
	ColumnSchema[] schemaR,schemaS,outputSchema;
	List<Datum[]> result;
	Iterator resultIter;
	HashMap<Long,File> bucketR;
	HashMap<Long,File> bucketS;
	
	HashSet<Long> keySetR;
	HashSet<Long> keySetS;
	
	int indexR,indexS;
	
	//LinkedList<File> bucketListR;
	//LinkedList<File> bucketListS;
	
	File swapDir;
	
	public HybridHashJoinOperator(Operator R, Operator S, int indexR, int indexS, String swapDir) throws CastingException, IOException{
		
		this.R = R; this.S = S;
		this.indexR = indexR; this.indexS = indexS;
		schemaR = R.getSchema(); schemaS = S.getSchema();
		this.swapDir = new File(swapDir);
		bucketR = new HashMap<Long,File>();
		bucketS = new HashMap<Long,File>();
		createBuckets(this.R,indexR, bucketR);
		createBuckets(this.S,indexS, bucketS);
		keySetR = (HashSet<Long>) bucketR.keySet();
		keySetS = (HashSet<Long>) bucketS.keySet();
		
		this.join();
		this.cleanup();
		this.updateSchema();
		resultIter = result.iterator();
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
	
	public void createBuckets(Operator O, int index, HashMap<Long,File> bucket) throws CastingException, IOException{
		Datum[] tuple;
		long key;
		File file;
		BufferedWriter bw;
		StringBuilder buildTuple = new StringBuilder();
		while((tuple = O.readOneTuple()) != null){
			for(int i = 0; i<tuple.length; i++){
				if(i == tuple.length - 1){
					buildTuple.append(tuple[i]);
				}else{
					buildTuple.append(tuple[i]+"|");
				}
			}
			key = tuple[index].toLONG();
			
			if(bucket.containsKey(key)){
				file = bucket.get(key);
				bw = new BufferedWriter(new FileWriter(file,true));
				bw.write(buildTuple.toString());
				bw.newLine();
				bw.flush();
				bw.close();
			}
			else{
				file = File.createTempFile("bkt", "tmp", swapDir);
				bw = new BufferedWriter(new FileWriter(file,true));
				bw.write(buildTuple.toString());
				bw.newLine();
				bw.flush();
				bw.close();
				bucket.put(key, file);
			}
		}
	}
	
	public void join() throws IOException, CastingException{
		Iterator iterR = keySetR.iterator();
		Datum[] outTuple = new Datum[R.getSchema().length + S.getSchema().length];
		result = new LinkedList<Datum[]>();
		while(iterR.hasNext()){
			
			Object key = iterR.next();
			File bucketFileR = bucketR.get(key);
			HashMap<Long, List<Datum[]>> build = new HashMap<Long, List<Datum[]>>();
			if(keySetS.contains(key)){
				File bucketFileS = bucketS.get(key);
				long size = bucketFileR.length();
				BufferedReader br = new BufferedReader(new FileReader(bucketFileR), (int)size);
				String line;
				// build secondary hash
				while((line = br.readLine())!= null){
					Datum[] tuple = parseLine(line, R.getSchema());
					List<Datum[]> r_list = build.get(tuple[indexR]);
					if(r_list == null){
						r_list = new LinkedList<Datum[]>();
					}
					r_list.add(tuple);
					build.put(tuple[indexR].toLONG(), r_list);
				}
				br.close();
				bucketR.remove(key);
				bucketFileR.delete();
				
				size = bucketFileS.length();
				br = new BufferedReader(new FileReader(bucketFileS),(int)size);
				// probing the hash table
				while((line = br.readLine()) != null){
					Datum[] tupleS  = parseLine(line, S.getSchema());
					List<Datum[]> r_list = build.get(tupleS[indexS]);
					if(r_list != null){
						for(Datum[] tupleR : r_list){
							int counter = 0;
							for(int i = 0; i<tupleR.length; i++){
								outTuple[counter] = tupleR[i];
								counter++;
							}
							for(int i=0; i<tupleS.length; i++){
								outTuple[counter] = tupleS[i];
								counter++;
							}
							result.add(outTuple);
						}
					}
				}
				br.close();
				bucketS.remove(key);
				bucketFileS.delete();
			}
			else{
				
				bucketR.remove(key);
				bucketFileR.delete();
			}				
		}
	}
	
	public Datum[] parseLine(String line, ColumnSchema[] schema){
		Datum[] ret = new Datum[schema.length];
		String[] cols = line.split("\\|");
		for(int i=0;i<ret.length;i++){
			if(schema[i].getType() == Datum.Type.LONG){
				ret[i] = new Datum.LONG(Long.parseLong(cols[i]));
			}else if(schema[i].getType() == Datum.Type.FLOAT){
				ret[i] = new Datum.FLOAT(Double.parseDouble(cols[i]));
			}else if(schema[i].getType() == Datum.Type.STRING){
				ret[i] = new Datum.STRING(cols[i]);
			}else if(schema[i].getType() == Datum.Type.BOOL){
				ret[i] = new Datum.BOOL(Boolean.parseBoolean(cols[i]));
			}else if(schema[i].getType() == Datum.Type.DATE){
				ret[i] = new Datum.DATE(cols[i]);
			}
			
		}
		return ret;
	}
	
	public void cleanup(){
		File file;
		Object key;
		Iterator iter = keySetR.iterator();
		while(iter.hasNext()){
			key = iter.next();
			file = bucketR.get(key);
			file.delete();
		}
		iter = keySetS.iterator();
		while(iter.hasNext()){
			key = iter.next();
			file = bucketS.get(key);
			file.delete();
		}
	}
	
	@Override
	public Datum[] readOneTuple() {
		if(resultIter.hasNext()){
			return (Datum[])resultIter.next();
		}
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public ColumnSchema[] getSchema() {
		return outputSchema;
	}

}