package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;

public class HybridHashJoinOperator implements Operator {

    //todo this is a placeholder
    public HybridHashJoinOperator(Operator input1, Operator input2, int index1, int index2) {
    }

    @Override
    public Datum[] readOneTuple() {
        return new Datum[0];
    }

    @Override
    public void reset() {

    }

    @Override
    public ColumnSchema[] getSchema() {
        return new ColumnSchema[0];
    }
}
