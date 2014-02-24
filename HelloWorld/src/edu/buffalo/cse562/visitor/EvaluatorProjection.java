package edu.buffalo.cse562.visitor;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.schema.Column;

public class EvaluatorProjection extends AbstractExpressionVisitor {

	Expression expression;
	String alias;
	
	public EvaluatorProjection(Expression expression, String alias) {
		this.expression = expression;
		this.alias = alias;
	}
	
	@Override
	public void visit(Function arg0) {
		// TODO Auto-generated method stub
		super.visit(arg0);
	}

	@Override
	public void visit(DoubleValue arg0) {
		// TODO Auto-generated method stub
		super.visit(arg0);
	}

	@Override
	public void visit(LongValue arg0) {
		// TODO Auto-generated method stub
		super.visit(arg0);
	}

	@Override
	public void visit(Addition arg0) {
		// TODO Auto-generated method stub
		super.visit(arg0);
	}

	@Override
	public void visit(Division arg0) {
		// TODO Auto-generated method stub
		super.visit(arg0);
	}

	@Override
	public void visit(Multiplication arg0) {
		// TODO Auto-generated method stub
		super.visit(arg0);
	}

	@Override
	public void visit(Subtraction arg0) {
		// TODO Auto-generated method stub
		super.visit(arg0);
	}

	@Override
	public void visit(Column arg0) {
		// TODO Auto-generated method stub
		super.visit(arg0);
	}

}
