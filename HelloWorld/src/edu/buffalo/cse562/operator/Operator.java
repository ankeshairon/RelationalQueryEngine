package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;

public interface Operator {
    public Datum[] readOneTuple();

    public void reset();

    public ColumnSchema[] getSchema();

    public Long getProbableTableSize();
}
