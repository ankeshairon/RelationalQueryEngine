package edu.buffalo.cse562.model.operators.aggregate.aggregationobject;

import edu.buffalo.cse562.model.data.Tuple;
import net.sf.jsqlparser.expression.Function;

public class AverageAggregation extends Aggregation {
    private Long sum;
    private Integer count;

    public AverageAggregation(Function aggregationFunction, Integer indexInOldSchema) {
        super(aggregationFunction, indexInOldSchema);
        sum = 0L;
        count = 0;
    }

    @Override
    public void process(Tuple tuple) {
        String fieldValue = tuple.fields.get(oldSchemaIndex);
        if (!"".equals(fieldValue)) {
            try {
                sum = sum + Integer.parseInt(fieldValue);
                count++;
            } catch (NumberFormatException e) {
                System.out.println("Warning!! Non-numeric data not supported for addition. Skipping current tuple");
            }
        }

    }

    @Override
    public String getValue() {
        return String.valueOf(sum / count);
    }

}
