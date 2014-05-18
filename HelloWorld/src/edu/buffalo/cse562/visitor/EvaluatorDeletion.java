package edu.buffalo.cse562.visitor;


import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;

public class EvaluatorDeletion extends AbstractExpressionVisitor {

    static boolean returnFlag= false;

    @Override
    public void visit(GreaterThanEquals arg0) {
        if(arg0.getLeftExpression() >= arg0.getRightExpression())
            returnFlag = true;
    }

    @Override
    public void visit(MinorThanEquals arg0) {
        if(arg0.getLeftExpression() <= arg0.getRightExpression())
            returnFlag = true;
    }
}
