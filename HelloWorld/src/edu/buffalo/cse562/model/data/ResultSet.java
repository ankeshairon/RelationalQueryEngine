/*
 * Author: Subhendu Saha
 * ResultSet class defined
 */

package edu.buffalo.cse562.model.data;

import java.util.ArrayList;
import java.util.Iterator;

public class ResultSet {
    public ArrayList<String> schema;
    public ArrayList<Tuple> tuples;

    public ResultSet(ArrayList<String> schema, ArrayList<Tuple> tuples) {
        this.schema = schema;
        this.tuples = tuples;
    }

    Iterator iterator;

    public boolean hasNext(){
        if (iterator.hasNext())
            return true;
		else
			return false;
	}
	
	public Tuple next(){
        return (Tuple) iterator.next();
    }
}
