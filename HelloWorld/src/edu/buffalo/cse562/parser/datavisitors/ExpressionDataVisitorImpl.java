package edu.buffalo.cse562.parser.datavisitors;

import edu.buffalo.cse562.parser.defaultimpl.AbstractExpressionVisitor;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;

public class ExpressionDataVisitorImpl extends AbstractExpressionVisitor {

    @Override
    public void visit(GreaterThanEquals gte) {
        super.visit(gte);
    }

    @Override
    public void visit(GreaterThan gt) {
        super.visit(gt);
    }

    @Override
    public void visit(EqualsTo et) {
        super.visit(et);
    }

    @Override
    public void visit(AndExpression ae) {
        super.visit(ae);
    }

    @Override
    public void visit(OrExpression oe) {
        super.visit(oe);
    }

    @Override
    public void visit(Multiplication m) {
        super.visit(m);
    }

    @Override
    public void visit(Subtraction s) {
        super.visit(s);
    }

    @Override
    public void visit(Addition adtn) {
        super.visit(adtn);
    }

    @Override
    public void visit(Division dvsn) {
        super.visit(dvsn);
    }

    @Override
    public void visit(BitwiseAnd ba) {
        super.visit(ba);
    }

    @Override
    public void visit(BitwiseOr bo) {
        super.visit(bo);
    }
}
