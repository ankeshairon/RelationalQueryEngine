/*Author - Ankesh Airon
 *ankeshai@buffalo.edu
 *Person no - 50096547
 *
 *Co-Author: Dev Bharadwaj
*/
package edu.buffalo.cse562.model.operators;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;

import edu.buffalo.cse562.model.data.ResultSet;
import edu.buffalo.cse562.model.data.Tuple;
import edu.buffalo.cse562.model.operatorabstract.UnaryOperator;
import edu.buffalo.cse562.model.operators.utils.OperatorUtils;
import edu.buffalo.cse562.parser.datavisitors.ExpressionTreeExecutor;
import edu.buffalo.cse562.processor.ExpressionTree;

public class SelectionOperator implements UnaryOperator {

    private ResultSet[] data;
    private ExpressionTree expressionTree;
    private HashSet columnNames;
    
    public SelectionOperator(ExpressionTree expressionTree, HashSet<String> columnNames) {
    	this.expressionTree = expressionTree;  
    	this.columnNames = columnNames;
    }
	
    @Override
    public void dataIn(ResultSet[] data) {
        //ExpressionTreeExecutor expressionTreeExecutor = new ExpressionTreeExecutor();
    	// Iterate over the data and push it into the tree if overall value returned is true,
    	// We are good, otherwise discard the tuple
        /*ArrayList<String> schema = data[0].getSchema(); 
        int index = 0;
        for (String column: schema) {
        	index++;
        	
        }
        ListIterator<Tuple> listIteratorX =  data[0].getTuplesListIteratorFromLastElement();
        while(listIteratorX.hasPrevious()){
        	Tuple meraTuple = listIteratorX.previous();
        }
       */
    	this.data = data;
        ArrayList<Tuple> workingSet = new ArrayList<>();
        Tuple currentTuple;
        ListIterator<Tuple>  listIterator;
        for (int i = 0; i < data.length; i++) {
        	listIterator = data[i].getTuplesListIteratorFromLastElement();
        	currentTuple = listIterator.previous();
        	workingSet.add(currentTuple);
        	if (workingSet.size() == data.length) {
        		ExpressionTreeExecutor expressionTreeRun = new ExpressionTreeExecutor(workingSet);
        		boolean toAdd = expressionTreeRun.getToAdd();
        		//if toAdd is false remove tuple
        		workingSet = null;
        		workingSet = new ArrayList<>();
        		i = 0;
        	}
        }
        
        this.queryRunner(data.length - 1, workingSet);
    }
    
    public void queryRunner(int level, ArrayList<Tuple> workingSet) {
    	if (level == 0) {
    		return;
    	}
    	else {
    		ListIterator<Tuple> listIterator =  data[level].getTuplesListIteratorFromLastElement();
    		while(listIterator.hasPrevious()) {
    	        	Tuple myTuple = listIterator.previous();
    	        	workingSet.add(myTuple);
    	        	level--;
    	        	queryRunner(level, workingSet);
    		}
    	}
    }

    @Override
    public ResultSet dataOut() {
        return null;
    }
}
