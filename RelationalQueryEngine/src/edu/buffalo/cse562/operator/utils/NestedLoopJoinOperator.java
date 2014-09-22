package edu.buffalo.cse562.operator.utils;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.operator.abstractoperators.JoinOperator;
import edu.buffalo.cse562.operator.abstractoperators.Operator;

public class NestedLoopJoinOperator extends JoinOperator {
    Datum[] temp1;
    Datum[] temp2;

    public NestedLoopJoinOperator(Operator R, Operator S) {
        super(R, S);
        temp1 = new Datum[R.getSchema().length];
        temp2 = new Datum[S.getSchema().length];
        temp1 = R.readOneTuple();
    }

    @Override
    public Datum[] readOneTuple() {
        Datum[] ret = new Datum[outputSchema.length];


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
}
