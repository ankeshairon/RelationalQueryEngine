package edu.buffalo.cse562.visitor;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

public class AbstractExpressionVisitor implements ExpressionVisitor {

    @Override
    public void visit(NullValue arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Function arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(InverseExpression arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(JdbcParameter arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(DoubleValue arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(LongValue arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(DateValue arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(TimeValue arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(TimestampValue arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Parenthesis arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(StringValue arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Addition arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Division arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Multiplication arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Subtraction arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(AndExpression arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(OrExpression arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Between arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(EqualsTo arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(GreaterThan arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(GreaterThanEquals arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(InExpression arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IsNullExpression arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(LikeExpression arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(MinorThan arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(MinorThanEquals arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NotEqualsTo arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Column arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(SubSelect arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(CaseExpression arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(WhenClause arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ExistsExpression arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(AllComparisonExpression arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(AnyComparisonExpression arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Concat arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Matches arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(BitwiseAnd arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(BitwiseOr arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(BitwiseXor arg0) {
        // TODO Auto-generated method stub

    }

}