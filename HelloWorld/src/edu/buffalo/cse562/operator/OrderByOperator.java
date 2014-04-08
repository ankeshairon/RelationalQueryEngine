package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.comparator.TupleComparator;
import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;

import java.util.*;

public class OrderByOperator implements Operator {

    Operator input;
    private LinkedHashMap<Integer, Boolean> indexesOfColumnsToSortOn;
    private Iterator<Datum[]> tupleListIterator;
    private List<Datum[]> tupleList;


    public OrderByOperator(Operator input, LinkedHashMap<Integer, Boolean> indexesOfColumnsToSortOn) {
        this.input = input;
        this.indexesOfColumnsToSortOn = indexesOfColumnsToSortOn;
        tupleList = new ArrayList<>();
        pullAllData();
        reset();
    }

    @Override
    public Datum[] readOneTuple() {
        if (tupleListIterator.hasNext()) {
            return tupleListIterator.next();
        }
        return null;
    }

    private void pullAllData() {
        Datum tuple[];
        while ((tuple = input.readOneTuple()) != null) {
            tupleList.add(tuple);
        }
        Collections.sort(tupleList, new TupleComparator(indexesOfColumnsToSortOn));
        reset();
    }

    @Override
    public void reset() {
        tupleListIterator = tupleList.iterator();
    }

    @Override
    public ColumnSchema[] getSchema() {
        return input.getSchema();
    }

    @Override
    public Long getProbableTableSize() {
        return input.getProbableTableSize();
    }

}
