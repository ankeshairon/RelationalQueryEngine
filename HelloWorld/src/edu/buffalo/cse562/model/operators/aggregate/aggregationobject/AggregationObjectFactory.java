package edu.buffalo.cse562.model.operators.aggregate.aggregationobject;

import edu.buffalo.cse562.model.operators.aggregate.AggregationType;
import net.sf.jsqlparser.expression.Function;

public class AggregationObjectFactory {
    public static Aggregation getAggregationObject(Function aggregationFunction, Integer indexInOldSchema) {
        switch (AggregationType.getAggregationType(aggregationFunction.getName())) {
            case SUM:
                return new SumAggregation(aggregationFunction, indexInOldSchema);
            case COUNT:
                return new CountAggregation(aggregationFunction, indexInOldSchema);
            case AVERAGE:
                return new AverageAggregation(aggregationFunction, indexInOldSchema);
        }
        return null;
    }
}
