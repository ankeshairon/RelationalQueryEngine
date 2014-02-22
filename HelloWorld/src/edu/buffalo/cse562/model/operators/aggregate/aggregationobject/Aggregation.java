package edu.buffalo.cse562.model.operators.aggregate.aggregationobject;

import edu.buffalo.cse562.model.data.Tuple;
import net.sf.jsqlparser.expression.Function;

public abstract class Aggregation {
    String oldColumnName;
    Integer oldSchemaIndex;
    String newColumnName;

    public Aggregation(Function aggregationFunction, Integer indexInOldSchema) {
        oldColumnName = aggregationFunction.getParameters().getExpressions().get(0).toString();
        newColumnName = aggregationFunction.toString();
        oldSchemaIndex = indexInOldSchema;
    }

    public String getNewColumnName() {
        return newColumnName;
    }

    public abstract String getValue();

    public abstract void process(Tuple tuple);
}

