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


public class AggregateOperator extends UnaryOperator{

	@Override
	public void dataIn(){
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	public void dataOut() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
