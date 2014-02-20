/*
 * @author: Dev Bharadwaj
 * Abstract class for Unary Operators:
 * 
 * 1. Selection
 * 2. Projection
 * 3. Aggregate
 * 
 */
package edu.buffalo.cse562.model.operatorabstract;


import edu.buffalo.cse562.mock.Datum;

public class UnaryOperator implements Operator {


    @Override
    public void dataIn(Datum data) {

    }

    @Override
    public Datum dataOut() {
        return null;
    }
}
