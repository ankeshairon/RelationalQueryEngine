/*
 *
 * 1. Sum
 * 2. Count
 * 3. Average
 *
 */

package edu.buffalo.cse562.model.operators.aggregate;


import edu.buffalo.cse562.model.data.ResultSet;
import edu.buffalo.cse562.model.data.Tuple;
import edu.buffalo.cse562.model.operatorabstract.UnaryOperator;
import edu.buffalo.cse562.model.operators.aggregate.aggregationobject.Aggregation;
import edu.buffalo.cse562.model.operators.aggregate.aggregationobject.AggregationObjectFactory;
import net.sf.jsqlparser.expression.Function;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;

import static edu.buffalo.cse562.model.operators.utils.OperatorUtils.calculateIndicesOfTheseDataColumns;

public class AggregateOperator implements UnaryOperator {

    //todo handle aggregates like sum(extendedprice*(1-discount))

    private ResultSet resultSet;
    private LinkedHashMap<Integer, Function> aggregationFunctions;

    LinkedHashMap<Integer, Long> sums;
    LinkedHashMap<Integer, Integer> counts;

    public AggregateOperator(LinkedHashMap<Integer, Function> aggregationFunctions) {
        this.aggregationFunctions = aggregationFunctions;
        sums = new LinkedHashMap<>();
        counts = new LinkedHashMap<>();
    }

    @Override
    public void dataIn(ResultSet[] inputDataSet) {
        // extract old schema to know the position of data in input
        // new data holders - read aggregationFunctions (sum, count, average)
        //                      and create maps of <integer,integer> "sum, counts, averages" which are
        //                      <indexes of columns based on old schema, their value/count>
        // new data creation - read each row and update each of the counters
        // prepare result set - read the old schema and map the results back

        List<Aggregation> aggregations = createAggregations(inputDataSet[0]);
        processAggregates(inputDataSet[0], aggregations);
        createNewResultSet(aggregations);
    }

    private void createNewResultSet(List<Aggregation> aggregations) {
        ArrayList<Tuple> newSingleTuple = new ArrayList<>();
        Tuple tuple = new Tuple();
        newSingleTuple.add(tuple);

        ArrayList<String> newSchema = new ArrayList<>();

        for (Aggregation aggregation : aggregations) {
            newSchema.add(aggregation.getNewSchemaIndex(), aggregation.getNewColumnName());
            tuple.fields.add(aggregation.getNewSchemaIndex(), aggregation.getValue());
        }
        resultSet = new ResultSet(newSchema, newSingleTuple);
    }

    @Override
    public ResultSet dataOut() {
        return resultSet;
    }

    private void processAggregates(ResultSet inputDataSet, List<Aggregation> aggregations) {
        ListIterator<Tuple> listIterator = inputDataSet.getTuplesListIteratorFromFirstElement();
        Tuple tuple;
        while (listIterator.hasNext()) {
            tuple = listIterator.next();
            for (Aggregation aggregation : aggregations) {
                aggregation.process(tuple);
            }
        }
    }

    private List<Aggregation> createAggregations(ResultSet inputDataSet) {
        List<Aggregation> aggregations = new ArrayList<>();

        for (Integer indexInNewSchema : aggregationFunctions.keySet()) {
            Function aggregationFunction = aggregationFunctions.get(indexInNewSchema);
            aggregations.add(AggregationObjectFactory.getAggregationObject(
                    aggregationFunction,
                    indexInNewSchema,
                    calculateIndicesOfTheseDataColumns(inputDataSet.getSchema(),
                            getColumnName(aggregationFunction)))
            );
        }
        return aggregations;
    }

    private String getColumnName(Function aggregationFunction) {
        return aggregationFunction.getParameters().getExpressions().get(0).toString();
    }
}
