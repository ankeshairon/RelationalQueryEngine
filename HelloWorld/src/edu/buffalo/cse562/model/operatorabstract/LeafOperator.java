/* 
 * @author: Dev Bharadwaj
 * Abstract class for Leaf Operators
 * 
 * 1. Source
 * 2. In
 * 3. Exists
 * 
 */

package edu.buffalo.cse562.model.operatorabstract;


import edu.buffalo.cse562.model.data.Datum;

public class LeafOperator implements Operator {

    @Override
    public void dataIn(Datum data) {
        throw new UnsupportedOperationException("Not supported yet.");

    }

    @Override
    public Datum dataOut() {
        throw new UnsupportedOperationException("Not supported yet.");

    }
}
