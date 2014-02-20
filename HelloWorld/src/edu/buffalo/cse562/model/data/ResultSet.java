/*
 * Author: Subhendu Saha
 * ResultSet class defined
 */

package edu.buffalo.cse562.model.data;

import java.util.*;

public class ResultSet {
	public ArrayList<String> schema;
	public ArrayList<Tuple> tuples;
	
	Iterator it;
	
	public ResultSet(ArrayList<String> a,ArrayList<Tuple> b){
		schema = a;
		tuples = b;
		it = tuples.iterator();
	}
	
	public boolean hasNext(){
		if(it.hasNext())
			return true;
		else
			return false;
	}
	
	public Tuple next(){
		return (Tuple)it.next();
	}
}
