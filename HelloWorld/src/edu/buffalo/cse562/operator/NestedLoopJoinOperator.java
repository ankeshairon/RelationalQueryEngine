package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;

public class NestedLoopJoinOperator extends JoinOperator {
    Datum[] temp1;
    Datum[] temp2;
    ColumnSchema[] schema;

    public NestedLoopJoinOperator(Operator R, Operator S) {
        super(R, S);
        updateSchema();
        temp1 = new Datum[R.getSchema().length];
        temp2 = new Datum[S.getSchema().length];
        temp1 = R.readOneTuple();
    }

    public void updateSchema() {
        int size1 = R.getSchema().length;
        int size2 = S.getSchema().length;
        schema = new ColumnSchema[size1 + size2];
        int i = 0;
        for (ColumnSchema cs : R.getSchema()) {
            schema[i] = cs;
            i++;
        }
        for (ColumnSchema cs : S.getSchema()) {
            schema[i] = cs;
            i++;
        }
    }

    @Override
    public Datum[] readOneTuple() {
        Datum[] ret = new Datum[schema.length];


        while (temp1 != null) {
            while ((temp2 = S.readOneTuple()) != null) {
                int counter = 0;
                for (Datum aTemp1 : temp1) {
                    ret[counter] = aTemp1;
                    counter++;
                }
                for (Datum aTemp2 : temp2) {
                    ret[counter] = aTemp2;
                    counter++;
                }
                return ret;
            }
            temp1 = R.readOneTuple();
            S.reset();
        }

        return null;
    }

    @Override
    public void reset() {
        R.reset();
        S.reset();
        temp1 = R.readOneTuple();
    }

    @Override
    public ColumnSchema[] getSchema() {
        return schema;
    }

    @Override
    public Long getProbableTableSize() {
        return R.getProbableTableSize() * S.getProbableTableSize();
    }

}
