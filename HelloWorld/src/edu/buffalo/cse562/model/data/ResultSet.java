/*
 * Author: Subhendu Saha
 * ResultSet class defined
 */

package edu.buffalo.cse562.model.data;

import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

import java.util.ArrayList;
import java.util.Iterator;

public class ResultSet {
    public ArrayList<ColumnDefinition> schema;
    public ArrayList<Tuple> tuples;
    private Iterator<Tuple> tuplesIterator;

    public ResultSet(ArrayList<ColumnDefinition> schema, ArrayList<Tuple> tuples) {
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
