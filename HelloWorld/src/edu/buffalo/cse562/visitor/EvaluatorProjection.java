package edu.buffalo.cse562.visitor;

import java.util.List;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
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
		String aggregate = arg0.getName();
		ExpressionList expressionList = arg0.getParameters();
		boolean isAllColumns = arg0.isAllColumns();
		boolean isDistinct = arg0.isDistinct();
		
		/*
		 * Aggregates don't follow the pull model 
		 * We need to implement the is_done function()
		 * This is to be a blocking method.
		 */
		System.out.println(aggregate + ", All columns " + isAllColumns + ", Distinct " + isDistinct);
		List<Expression> expr;
		while ((expr = expressionList.getExpressions()) != null) {
			for (Expression e: expr){
			System.out.println(e);
			}
		}

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
