package edu.buffalo.cse562.model.operators.aggregate.aggregationobject;

import edu.buffalo.cse562.model.data.Tuple;
import net.sf.jsqlparser.expression.Function;

public class CountAggregation extends Aggregation {
    private Integer value;

    public CountAggregation(Function aggregationFunction, Integer indexInOldSchema) {
        super(aggregationFunction, indexInOldSchema);
        value = 0;
    }

    @Override
    public void process(Tuple tuple) {
        if (!"".equals(tuple.fields.get(oldSchemaIndex))) {
            value++;
        }
    }

    @Override
    public String getValue() {
        return String.valueOf(value);
    }
}
