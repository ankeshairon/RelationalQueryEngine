/*
 * @author: Dev Bharadwaj
 * An element (column reference) in an "ORDER BY" clause.
 * 
 */

package edu.buffalo.cse562.parser.datavisitors;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.OrderByVisitor;

public class OrderByVisitorImpl implements OrderByVisitor {

    Expression columnReference = null;

    public Expression getExpression() {
        return columnReference;
    }

    @Override
    public void visit(OrderByElement orderByElement) {
        columnReference = orderByElement.getExpression();
    }

}
