/*Author - Ankesh Airon
 *ankeshai@buffalo.edu
 *Person no - 50096547
 *
 *Co-Author: Dev Bharadwaj
*/
package edu.buffalo.cse562.model.operators;

import java.util.ArrayList;
import java.util.ListIterator;

import edu.buffalo.cse562.model.data.ResultSet;
import edu.buffalo.cse562.model.data.Tuple;
import edu.buffalo.cse562.model.operatorabstract.UnaryOperator;
import edu.buffalo.cse562.parser.datavisitors.ExpressionTreeExecutor;
import edu.buffalo.cse562.processor.ExpressionTree;

public class SelectionOperator implements UnaryOperator {

    private ResultSet data;
    private ExpressionTree expressionTree;
    
    public SelectionOperator(ExpressionTree expressionTree) {
    	this.expressionTree = expressionTree;  
    }
	
    @Override
    public void dataIn(ResultSet[] data) {
        ExpressionTreeExecutor expressionTreeExecutor = new ExpressionTreeExecutor();
    	// Iterate over the data and push it into the tree if overall value returned is true,
    	// We are good, otherwise discard the tuple
        ListIterator<Tuple> listIterator =  data[0].getTuplesListIteratorFromLastElement();
        while(listIterator.hasPrevious()){
        	Tuple meraTuple = listIterator.previous();
        }
    }

    @Override
    public ResultSet dataOut() {
        return null;
    }
}
