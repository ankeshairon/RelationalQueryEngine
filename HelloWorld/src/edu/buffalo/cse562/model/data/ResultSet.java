/*
 * Author: Subhendu Saha
 * ResultSet class defined
 */

package edu.buffalo.cse562.model.data;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ResultSet {
    private List<Integer> indexesOfColumnsToDisplay;
    private List<String> schema;
    private List<Tuple> tuples;

    public ResultSet(List<String> schema, List<Tuple> tuples) {
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

    public List<String> getSchema() {
        return schema;
    }

    public List<Tuple> getTuples() {
        return tuples;
    }

    private void instantiateListOfColumnIndexesToDisplay() {
        indexesOfColumnsToDisplay = new ArrayList<>();
        for (int i = 0; i < schema.size(); i++) {
            indexesOfColumnsToDisplay.add(i);
        }
    }

}
