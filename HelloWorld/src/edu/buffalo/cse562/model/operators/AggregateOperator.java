/*
 * @author: Dev Bharadwaj
 * Abstract class for the operators:
 * 
 * 1. Sum
 * 2. Count
 * 3. Average
 * 4. Limit
 * 
 */

package edu.buffalo.cse562.model.operators;


import edu.buffalo.cse562.model.data.ResultSet;
import edu.buffalo.cse562.model.data.Tuple;
import edu.buffalo.cse562.model.operatorabstract.UnaryOperator;
import net.sf.jsqlparser.expression.Function;

import java.util.LinkedHashMap;
import java.util.ListIterator;

public class AggregateOperator implements UnaryOperator {


    private ResultSet resultSet;
    private LinkedHashMap<Integer, Function> aggregationExpressions;

    LinkedHashMap<Integer, Long> sums;
    LinkedHashMap<Integer, Integer> counts;

    public AggregateOperator(LinkedHashMap<Integer, Function> aggregationExpressions) {
        this.aggregationExpressions = aggregationExpressions;
        sums = new LinkedHashMap<>();
        counts = new LinkedHashMap<>();
    }

    @Override
    public void dataIn(ResultSet[] inputDataSet) {
        // extract old schema to know the position of data in input
        // new data holders - read aggregationExpressions (sum, count, average)
        //                      and create maps of <integer,integer> "sum, counts, averages" which are
        //                      <indexes of columns based on old schema, their value/count>
        // new data creation - read each row and update each of the counters
        // prepare result set - read the old schema and map the results back

        createListsOfAggregatesToFind();
        calculateAggregates(inputDataSet[0]);


//        OperatorUtils.calculateIndicesOfTheseDataColumns()

    }

    private void calculateAggregates(ResultSet inputDataSet) {
        ListIterator<Tuple> listIterator = inputDataSet.getTuplesListIteratorFromFirstElement();
        Tuple currentTuple;

        while (listIterator.hasNext()) {
            currentTuple = listIterator.next();

//            if (currentTuple.fields.get(indexOfFieldOfInterest) != null) {
//                incrementCounter;
//                addToSum
//            }

        }
    }

    private void createListsOfAggregatesToFind() {
        for (Integer indexInNewSchema : aggregationExpressions.keySet()) {
            switch (aggregationExpressions.get(indexInNewSchema).getName()) {
                case "sum":
                case "SUM":
                    sums.put(indexInNewSchema, 0L);
                    break;
                case "COUNT":
                case "count":
                    counts.put(indexInNewSchema, 0);
                    break;
                case "AVG":
                case "avg":
                    sums.put(indexInNewSchema, 0L);
                    counts.put(indexInNewSchema, 0);
                    break;
            }
        }
    }

    @Override
    public ResultSet dataOut() {
        return resultSet;
    }
}
