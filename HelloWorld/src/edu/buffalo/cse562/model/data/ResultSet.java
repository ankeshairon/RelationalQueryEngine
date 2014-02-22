/*
 * Author: Subhendu Saha
 * ResultSet class defined
 */

package edu.buffalo.cse562.model.data;

import java.util.ArrayList;
import java.util.ListIterator;

public class ResultSet {
    private ArrayList<Integer> indexesOfColumnsToDisplay;
    private ArrayList<String> schema;
    private ArrayList<Tuple> tuples;

    public ResultSet(ArrayList<String> schema, ArrayList<Tuple> tuples) {
        this.schema = schema;
        this.tuples = tuples;
        instantiateListOfColumnIndexesToDisplay();
    }

    /**
     * Return iterator starting from the last element
     * Use hasPrevious() & previous() methods to access this
     */
    public ListIterator<Tuple> getTuplesListIteratorFromLastElement() {
        return tuples.listIterator(tuples.size());
    }

    public ListIterator<Tuple> getTuplesListIteratorFromFirstElement() {
        return tuples.listIterator();
    }

    public ArrayList<String> getSchema() {
        return schema;
    }

    public ArrayList<Tuple> getTuples() {
        return tuples;
    }

    private void instantiateListOfColumnIndexesToDisplay() {
        indexesOfColumnsToDisplay = new ArrayList<>();
        for (int i = 0; i < schema.size(); i++) {
            indexesOfColumnsToDisplay.add(i);
        }
    }

}
