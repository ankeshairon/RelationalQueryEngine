/*
 * @author: Dev Bharadwaj
 * Implemented for Projection to get the list of the columns
 * 
 */

package edu.buffalo.cse562.parser.datavisitors;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;

public class SelectItemVisitorImpl implements SelectItemVisitor {

	int columnsAll = 0;
	String columnNames = null;
	Expression columnExpression = null;
	
	public int getIfAllColumns() {
		return this.columnsAll;
	}
	
	public String getAllTableColumns() {
		return this.columnNames;
	}
	
	public Expression getExpression() {
		return this.columnExpression;
	}
	
	@Override
	public void visit(AllColumns arg0) {
		System.out.println("AllColumns" + arg0);
		this.columnsAll = 1;
	}

	@Override
	public void visit(AllTableColumns arg0) {
		System.out.println("AllTableColumns" + arg0);
		this.columnNames = arg0.toString();
	}

	@Override
	public void visit(SelectExpressionItem arg0) {
		System.out.println("SelectExpressionItem" + arg0);
		columnExpression = arg0.getExpression();
	}

}
