/*
 * @author: Dev Bharadwaj
 * Implemented for Projection to get the list of the columns
 * 
 */

package edu.buffalo.cse562.parser.datavisitors;

import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;

public class SelectItemVisitorImpl implements SelectItemVisitor {


    @Override
	public void visit(AllColumns arg0) {
	}

	@Override
	public void visit(AllTableColumns arg0) {
	}

	@Override
	public void visit(SelectExpressionItem arg0) {
	}

}
