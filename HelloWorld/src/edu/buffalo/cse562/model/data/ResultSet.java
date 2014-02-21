/*
 * Author: Subhendu Saha
 * ResultSet class defined
 */

package edu.buffalo.cse562.model.data;

import java.util.ArrayList;
import java.util.ListIterator;

public class ResultSet {
    private ArrayList<String> schema;
    private ArrayList<Tuple> tuples;

    public ResultSet(ArrayList<String> schema, ArrayList<Tuple> tuples) {
        this.schema = schema;
        this.tuples = tuples;
    }

    public ListIterator<Tuple> getTuplesListIteratorFromLastElement() {
        return tuples.listIterator(tuples.size());
    }

    public ArrayList<String> getSchema() {
        return schema;
    }

}
