package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;

public class JoinOperator implements Operator {
    Operator input1;
    Operator input2;
    Datum[] temp1;
    Datum[] temp2;
    ColumnSchema[] schema;

    public JoinOperator(Operator input1, Operator input2) {
        this.input1 = input1;
        this.input2 = input2;

        updateSchema();
        temp1 = new Datum[input1.getSchema().length];
        temp2 = new Datum[input2.getSchema().length];
        temp1 = input1.readOneTuple();
        //temp2 = input2.readOneTuple();
    }

    public void updateSchema() {
        int size1 = input1.getSchema().length;
        int size2 = input2.getSchema().length;
        schema = new ColumnSchema[size1 + size2];
        int i = 0;
        for (ColumnSchema cs : input1.getSchema()) {
            schema[i] = cs;
            i++;
        }
        for (ColumnSchema cs : input2.getSchema()) {
            schema[i] = cs;
            i++;
        }
    }

    @Override
    public Datum[] readOneTuple() {
        Datum[] ret = new Datum[schema.length];


        while (temp1 != null) {
            while ((temp2 = input2.readOneTuple()) != null) {
                int counter = 0;
                for (int i = 0; i < temp1.length; i++) {
                    ret[counter] = temp1[i];
                    counter++;
                }
                for (int i = 0; i < temp2.length; i++) {
                    ret[counter] = temp2[i];
                    counter++;
                }
                //temp2 = input2.readOneTuple();
                return ret;
            }
            temp1 = input1.readOneTuple();
            input2.reset();
        }

        return null;
    }

    @Override
    public void reset() {
        input1.reset();
        input2.reset();
        temp1 = input1.readOneTuple();
    }

    @Override
    public ColumnSchema[] getSchema() {
        return schema;
    }

}
