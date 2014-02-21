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

public class AggregateOperator implements UnaryOperator {


    private ResultSet resultSet;
    private Expression aggregationExpression;

    public AggregateOperator(Expression aggregationExpression) {
        this.aggregationExpression = aggregationExpression;
    }

    @Override
    public void dataIn(ResultSet resultSet) {

    }

    @Override
    public ResultSet dataOut() {
        return resultSet;
    }
}
