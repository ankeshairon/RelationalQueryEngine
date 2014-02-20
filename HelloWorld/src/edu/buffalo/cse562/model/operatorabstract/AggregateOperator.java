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

package edu.buffalo.cse562.model.operatorabstract;


import edu.buffalo.cse562.model.data.ResultSet;

public class AggregateOperator implements UnaryOperator {

    @Override
    public void dataIn(ResultSet data) {
    }

    @Override
    public ResultSet dataOut() {
        return null;
    }
}
