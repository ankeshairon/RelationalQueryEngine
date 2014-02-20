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


import edu.buffalo.cse562.model.data.Datum;

public class AggregateOperator extends UnaryOperator{

    @Override
    public void dataIn(Datum data) {
        super.dataIn(data);
    }

    @Override
    public Datum dataOut() {
        return super.dataOut();
    }
}
