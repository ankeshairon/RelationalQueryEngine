package edu.buffalo.cse562.model.operators.aggregate.aggregationobject;

import edu.buffalo.cse562.model.data.Tuple;
import net.sf.jsqlparser.expression.Function;

public abstract class Aggregation {
    String oldColumnName;
    Integer oldSchemaIndex;
    String newColumnName;
    Integer newSchemaIndex;

    public Aggregation(Function aggregationFunction, Integer indexInNewSchema, Integer indexInOldSchema) {
        oldColumnName = aggregationFunction.getParameters().getExpressions().get(0).toString();
        newColumnName = aggregationFunction.toString();
        newSchemaIndex = indexInNewSchema;
        oldSchemaIndex = indexInOldSchema;
    }

    public Integer getNewSchemaIndex() {
        return newSchemaIndex;
    }

    public String getNewColumnName() {
        return newColumnName;
    }

    public abstract String getValue();

    public abstract void process(Tuple tuple);
}

