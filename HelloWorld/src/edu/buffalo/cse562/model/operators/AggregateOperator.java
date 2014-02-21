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
import edu.buffalo.cse562.model.operatorabstract.UnaryOperator;
import edu.buffalo.cse562.model.operators.utils.OperatorUtils;
import net.sf.jsqlparser.expression.Expression;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AggregateOperator implements UnaryOperator {


    private ResultSet resultSet;
    private LinkedHashMap<Integer, Expression> aggregationExpressions;

    List<Integer> sums;
    List<Integer> counts;
    List<Integer> averages;

    public AggregateOperator(LinkedHashMap<Integer, Expression> aggregationExpressions) {
        this.aggregationExpressions = aggregationExpressions;
        sums = new ArrayList<>();
        counts = new ArrayList<>();
        averages = new ArrayList<>();
    }

    @Override
    public void dataIn(ResultSet[] inputDataSet) {
        // extract old schema to know the position of data in input
        // new data holders - read aggregationExpressions (sum, count, average)
        //                      and create maps of <integer,integer> "sum, counts, averages" which are
        //                      <indexes of columns based on old schema, their value/count>
        // new data creation - read each row and update each of the counters
        // prepare result set - read the old schema and map the results back


        for (Integer indexInOldSchema : aggregationExpressions.keySet()) {

        }


        OperatorUtils.calculateIndicesOfTheseDataColumns()

    }

    @Override
    public ResultSet dataOut() {
        return resultSet;
    }
}
