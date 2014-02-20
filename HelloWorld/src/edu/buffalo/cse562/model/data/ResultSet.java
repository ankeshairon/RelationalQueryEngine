/*
 * Author: Subhendu Saha
 * ResultSet class defined
 */

package edu.buffalo.cse562.model.data;

import java.util.*;

public class ResultSet {
	public ArrayList<String> schema;
	public ArrayList<Tuple> tuples;
	
	public ResultSet(ArrayList<String> a,ArrayList<Tuple> b){
		schema = a;
		tuples = b;
	}
}
