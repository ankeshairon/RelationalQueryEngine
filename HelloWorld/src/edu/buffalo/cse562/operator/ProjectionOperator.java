package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;

public class ProjectionOperator implements Operator {

    Operator input;
    ColumnSchema[] outputSchema;
    Integer[] indexes;

    public ProjectionOperator(Operator in, ColumnSchema[] outputSchema, Integer[] indexes) {
        input = in;
        this.outputSchema = outputSchema;
        this.indexes = indexes;
    }

    @Override
    public Datum[] readOneTuple() {
        Datum[] ret = new Datum[outputSchema.length];
        Datum[] tuple;
        if ((tuple = input.readOneTuple()) != null) {
            for (int i = 0; i < indexes.length; i++) {
                ret[i] = tuple[indexes[i]];
            }
            return ret;
        }
        return null;
    }

    @Override
    public void reset() {
        input.reset();
    }

    @Override
    public ColumnSchema[] getSchema() {
        return outputSchema;
    }

}