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
    private Iterator<Tuple> tuplesIterator;

    public ResultSet(ArrayList<String> schema, ArrayList<Tuple> tuples) {
        this.schema = schema;
        this.tuples = tuples;
        tuplesIterator = tuples.iterator();
    }

    public boolean hasNext() {
        return tuplesIterator.hasNext();
    }

    public Tuple next() {
        return tuplesIterator.next();
    }
}
