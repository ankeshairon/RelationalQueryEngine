/*
 * @author: Dev Bharadwaj
 * Implemented for Projection to get the list of the columns
 * 
 */

package edu.buffalo.cse562.parser.datavisitors;

import edu.buffalo.cse562.model.operators.ProjectionOperator;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;

public class SelectItemVisitorImpl implements SelectItemVisitor {


    private ProjectionOperator projectionOperator;

    public SelectItemVisitorImpl(ProjectionOperator projectionOperator) {
        this.projectionOperator = projectionOperator;
    }

    @Override
    public void visit(AllColumns allColumns) {
        //leave blank to keep the operator list empty
    }

	@Override
    public void visit(AllTableColumns allColumnsOfTable) {
        projectionOperator.addProjectionAttribute(allColumnsOfTable.toString());
    }

	@Override
    public void visit(SelectExpressionItem selectExpressionItem) {
        projectionOperator.addProjectionAttribute(selectExpressionItem.getExpression());
    }

}
