package edu.buffalo.cse562.operator.joins;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.operator.abstractoperators.FilterOperator;
import edu.buffalo.cse562.operator.abstractoperators.JoinOperator;
import edu.buffalo.cse562.operator.utils.indexscan.IndexScanHelper;

public class IndexNestedLoopJoinOperator extends JoinOperator {
    public IndexNestedLoopJoinOperator(FilterOperator operator1, FilterOperator operator2, Integer position1, Integer position2) {
        super(operator1, operator2);
        IndexScanHelper helper1 = new IndexScanHelper(operator1.getSchema(), null);
        IndexScanHelper helper2 = new IndexScanHelper(operator2.getSchema(), null);
    }

    @Override
    public Datum[] readOneTuple() {
        return new Datum[0];
    }

    @Override
    public void reset() {

    }
}
