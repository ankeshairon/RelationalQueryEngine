package edu.buffalo.cse562.parser.datavisitors;

import edu.buffalo.cse562.model.data.Tuple;
import edu.buffalo.cse562.parser.defaultimpl.AbstractExpressionVisitor;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;

import java.util.List;

public class ExpressionTreeExecutor extends AbstractExpressionVisitor {

    boolean toAdd = false;
    List<Tuple> workingSet;

    public ExpressionTreeExecutor(List<Tuple> workingSet) {
        this.workingSet = workingSet;
    }

    public boolean getToAdd() {
        return toAdd;
    }

    @Override
    public void visit(Function fnctn) {
        // Auto-generated method stub
        super.visit(fnctn);
    }

    @Override
    public void visit(DoubleValue dv) {
        // Auto-generated method stub
        super.visit(dv);
    }

    @Override
    public void visit(LongValue lv) {
        // Auto-generated method stub
        super.visit(lv);
    }

    @Override
    public void visit(DateValue dv) {
        // Auto-generated method stub
        super.visit(dv);
    }

    @Override
    public void visit(StringValue sv) {
        // Auto-generated method stub
        super.visit(sv);
    }

    @Override
    public void visit(Addition expr) {
        super.visit(expr);
    }

    @Override
    public void visit(Division dvsn) {
        // Auto-generated method stub
        super.visit(dvsn);
    }

    @Override
    public void visit(Multiplication m) {
        // Auto-generated method stub
        super.visit(m);
    }

    @Override
    public void visit(Subtraction s) {
        // Auto-generated method stub
        super.visit(s);
    }

    @Override
    public void visit(AndExpression ae) {
        // Auto-generated method stub
        super.visit(ae);
    }

    @Override
    public void visit(OrExpression oe) {
        // Auto-generated method stub
        super.visit(oe);
    }

    @Override
    public void visit(EqualsTo et) {
        // Auto-generated method stub
        super.visit(et);
    }

    @Override
    public void visit(GreaterThan gt) {
        // Auto-generated method stub
        super.visit(gt);
    }

    @Override
    public void visit(GreaterThanEquals gte) {
        // Auto-generated method stub
        super.visit(gte);
    }

    @Override
    public void visit(MinorThan mt) {
        // Auto-generated method stub
        super.visit(mt);
    }

    @Override
    public void visit(MinorThanEquals mte) {
        // Auto-generated method stub
        super.visit(mte);
    }

    @Override
    public void visit(Column column) {
        // Auto-generated method stub
        super.visit(column);
    }

}