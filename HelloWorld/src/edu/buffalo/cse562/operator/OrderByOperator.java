package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.comparator.TupleComparator;
import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;

import java.util.*;

public class OrderByOperator implements Operator {

    Operator input;
    private LinkedHashMap<Integer, Boolean> indexesOfColumnsToSortOn;
    private Iterator<Datum[]> tupleListIterator;


    public OrderByOperator(Operator input, LinkedHashMap<Integer, Boolean> indexesOfColumnsToSortOn) {
        this.input = input;
        this.indexesOfColumnsToSortOn = indexesOfColumnsToSortOn;
        pullAllData();
    }

    @Override
    public Datum[] readOneTuple() {
        if (tupleListIterator.hasNext()) {
            return tupleListIterator.next();
        }
        return null;
    }

    private void pullAllData() {
        List<Datum[]> tupleList = new ArrayList<>();
        Datum tuple[];
        while ((tuple = input.readOneTuple()) != null) {
            tupleList.add(tuple);
        }
        Collections.sort(tupleList, new TupleComparator(indexesOfColumnsToSortOn));
        tupleListIterator = tupleList.iterator();
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public ColumnSchema[] getSchema() {
        // TODO Auto-generated method stub
        return null;
    }

}
