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
import net.sf.jsqlparser.expression.Expression;

import java.util.LinkedHashMap;

public class AggregateOperator implements UnaryOperator {


    private ResultSet resultSet;
    private LinkedHashMap<Integer, Expression> aggregationExpressions;

//    private List<String>

    public AggregateOperator(LinkedHashMap<Integer, Expression> aggregationExpressions) {
        this.aggregationExpressions = aggregationExpressions;
    }

    @Override
    public void dataIn(ResultSet inputDataSet) {
        // extract old schema to know the position of data in input
        // read aggregationExpressions (sum, count, average) and create lists of integers "sum, counts, averages" which are indexes of columns based on old schema
        //

    }

    @Override
    public ResultSet dataOut() {
        return resultSet;
    }
}
