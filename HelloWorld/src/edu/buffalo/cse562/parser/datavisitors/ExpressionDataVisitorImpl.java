package edu.buffalo.cse562.parser.datavisitors;

import edu.buffalo.cse562.parser.defaultimpl.AbstractExpressionVisitor;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;

public class ExpressionDataVisitorImpl extends AbstractExpressionVisitor {

    @Override
    public void visit(GreaterThanEquals gte) {

    }

    @Override
    public void visit(GreaterThan gt) {
        gt.getLeftExpression();
    }
}
