package edu.buffalo.cse562.model.aggregation.factory;

import edu.buffalo.cse562.model.aggregation.Aggregation;
import edu.buffalo.cse562.model.aggregation.AverageAggregation;
import edu.buffalo.cse562.model.aggregation.CountAggregation;
import edu.buffalo.cse562.model.aggregation.SumAggregation;
import edu.buffalo.cse562.model.aggregation.type.AggregationType;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;

import java.util.List;

public class AggregationObjectFactory {

    public static Aggregation getAggregationObject(Function aggregationFunction, ColumnSchema[] oldSchema, List<Integer> indexesOfGroupByReferencesInOldSchema) {
        switch (AggregationType.getAggregationType(aggregationFunction.getName())) {
            case SUM:
                return new SumAggregation(oldSchema, (Expression) aggregationFunction.getParameters().getExpressions().get(0), indexesOfGroupByReferencesInOldSchema);
            case COUNT:
                return new CountAggregation(oldSchema, (Expression) aggregationFunction.getParameters().getExpressions().get(0), indexesOfGroupByReferencesInOldSchema);
            case AVERAGE:
                return new AverageAggregation(oldSchema, (Expression) aggregationFunction.getParameters().getExpressions().get(0), indexesOfGroupByReferencesInOldSchema);
        }
        return null;

    }
}
