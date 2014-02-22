package edu.buffalo.cse562.model.operators.aggregate.aggregationobject;

import edu.buffalo.cse562.model.data.Tuple;
import net.sf.jsqlparser.expression.Function;

public class SumAggregation extends Aggregation {

    private Long value;

    public SumAggregation(Function aggregationFunction, Integer indexInNewSchema, Integer indexInOldSchema) {
        super(aggregationFunction, indexInNewSchema, indexInOldSchema);
        value = 0L;
    }

    @Override
    public void process(Tuple tuple) {
        String fieldValue = tuple.fields.get(oldSchemaIndex);
        if (!"".equals(fieldValue)) {
            try {
                value = value + Integer.parseInt(fieldValue);
            } catch (NumberFormatException e) {
                System.out.println("Warning!! Non-numeric data not supported for addition. Skipping current tuple");
            }
        }
    }

    @Override
    public String getValue() {
        return String.valueOf(value);
    }
}
