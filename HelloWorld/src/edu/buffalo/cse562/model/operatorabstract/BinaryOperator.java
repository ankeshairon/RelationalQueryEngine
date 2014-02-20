/*
 * @author: Dev Bharadwaj
 * Abstract class for Binary Operators
 * 
 * 1. Join
 * 2. Union
 * 
 */

package edu.buffalo.cse562.model.operatorabstract;


import edu.buffalo.cse562.model.data.Datum;

public class BinaryOperator implements Operator {

    @Override
    public void dataIn(Datum data) {

    }

    @Override
    public Datum dataOut() {
        throw new UnsupportedOperationException("Not supported yet.");

    }

}
