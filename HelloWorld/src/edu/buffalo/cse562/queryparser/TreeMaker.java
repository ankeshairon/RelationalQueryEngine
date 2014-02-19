/*
 * @Author:  Dev Bharadwaj
 * Pass-in the PlainSelect object and create the logical list of operators
 * execute() is then called and data is passed in and out of the operators
 * We return the output back to PlainSelect.
 */

package edu.buffalo.cse562.queryparser;

import edu.buffalo.cse562.model.operatorabstract.Operator;
import edu.buffalo.cse562.model.operators.JoinOperator;
import edu.buffalo.cse562.model.operators.OrderByOperator;
import edu.buffalo.cse562.model.operators.ProjectionOperator;
import edu.buffalo.cse562.model.operators.SelectionOperator;
import edu.buffalo.cse562.model.operators.SourceOperator;
import edu.buffalo.cse562.parser.datavisitors.FromItemVisitorImpl;
import edu.buffalo.cse562.parser.datavisitors.OrderByVisitorImpl;
import edu.buffalo.cse562.parser.datavisitors.SelectItemVisitorImpl;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TreeMaker {
    private ArrayList<Operator> rAOperatorList;

    public TreeMaker(PlainSelect plainSelect) {
        this.rAOperatorList = new ArrayList<>();
        this.sourceOperatorAdd(plainSelect);
        this.selectOperatorAdd(plainSelect);
        this.projectOperatorAdd(plainSelect);
        this.orderbyOperatorAdd(plainSelect);
        this.groupbyOperatorAdd(plainSelect);
    }


	public void sourceOperatorAdd(PlainSelect plainSelect) {
    	FromItemVisitorImpl fromItemVisitorImpl = new FromItemVisitorImpl();
    	plainSelect.getFromItem().accept(fromItemVisitorImpl);
    	//Get the datum extracted from FromItem like this:
    	//Datum data1 = fromItemVisitorImpl.getDatum();
    	
    	List<Join> joins = plainSelect.getJoins();
    	
    	// if joins is not NULL then we have more than one source
    	// Check the type of join and extract condition if not natural join
    	
    	if (joins != null) {
    		//extract condition and get output from joins
    		for (Join j: joins){
    			//JoinOperator join = new JoinOperator();
    			//join.dataIn();
    			//data = join.dataOut();
    		}
    	}
    	
    	//join_datum + fromitem_datum = datum
    	//SourceOperator sourceOperator = new SourceOperator(datum);
    	//this.rAOperatorList.add(sourceOperator);
   }
    
    
    public void selectOperatorAdd(PlainSelect plainSelect) {
    	if (plainSelect.getWhere() != null) {
    		SelectionOperator selectOperator = new SelectionOperator();
    		selectOperator.setWhereCondition(plainSelect.getWhere());
    		this.rAOperatorList.add(selectOperator);
    	}
    }

    public void projectOperatorAdd(PlainSelect plainSelect) {
    		List<SelectItem> selectItem = plainSelect.getSelectItems();
    		SelectItemVisitorImpl selectItemVisitorImpl;
    		for (SelectItem item: selectItem) {
    			selectItemVisitorImpl = new SelectItemVisitorImpl();
    			item.accept(selectItemVisitorImpl);
    			if (selectItemVisitorImpl.getIfAllColumns() == 1) {
    				// get all columns, no projection
    			}
    			else if (selectItemVisitorImpl.getAllTableColumns() != null) {
    				// get string of specified columns
    			}
    			else if (selectItemVisitorImpl.getExpression() != null) {
    				// get expression and evaluate it
    				// An expression as in "SELECT expr1 AS EXPR", get alias and expression
    			}
    		}
    		this.rAOperatorList.add(new ProjectionOperator());
    }
    
    public void orderbyOperatorAdd(PlainSelect plainSelect) {
    	OrderByVisitorImpl orderByVisitorImpl;
    	ArrayList<Expression> orderByExpression = new ArrayList<>();
    	List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
    	for (OrderByElement e: orderByElements) {
    		orderByVisitorImpl = new OrderByVisitorImpl();
    		e.accept(orderByVisitorImpl);
    		orderByExpression.add(orderByVisitorImpl.getExpression());
    	}
    	this.rAOperatorList.add(new OrderByOperator(orderByExpression));
	}

	public void groupbyOperatorAdd(PlainSelect plainSelect) {
    	/**
    	 * A list of {@link Expression}s of the GROUP BY clause.
    	 * It is null in case there is no GROUP BY clause
    	 * @return a list of {@link Expression}s 
    	 */
		List<Expression> columnReference = plainSelect.getGroupByColumnReferences();
		// Inaccurate documentation about this.......
		// Sysout to see what we get here
	}   
/*    public Datum execute(Datum datum) {
    	for (Operator Ops : this.rAOperatorList) {
    		Ops.dataIn(datum);
    		datum = Ops.dataOut();
    	}
    	return Datum;
    }
*/
}
