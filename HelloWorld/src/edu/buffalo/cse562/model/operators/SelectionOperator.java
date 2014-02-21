/*Author - Ankesh Airon
 *ankeshai@buffalo.edu
 *Person no - 50096547
 *
 *Co-Author: Dev Bharadwaj
*/
package edu.buffalo.cse562.model.operators;

import edu.buffalo.cse562.model.data.ResultSet;
import edu.buffalo.cse562.model.operatorabstract.UnaryOperator;
import edu.buffalo.cse562.parser.datavisitors.ExpressionTreeExecutor;
import edu.buffalo.cse562.processor.ExpressionTree;
import net.sf.jsqlparser.expression.Expression;

public class SelectionOperator implements UnaryOperator {

    private ResultSet data;
    private ExpressionTree expressionTree;
    
    public SelectionOperator(ExpressionTree expressionTree) {
    	this.expressionTree = expressionTree;  
    }
	
    @Override
    public void dataIn(ResultSet data) {
    	ExpressionTreeExecutor expressionTreeExecutor = new ExpressionTreeExecutor();
    	// Iterate over the data and push it into the tree if overall value returned is true,
    	// We are good, otherwise discard the tuple
    }

    @Override
    public ResultSet dataOut() {
        return null;
    }
}