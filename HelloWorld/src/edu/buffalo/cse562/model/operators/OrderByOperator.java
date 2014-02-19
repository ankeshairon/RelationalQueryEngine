package edu.buffalo.cse562.model.operators;

import java.util.List;

import net.sf.jsqlparser.expression.Expression;

import edu.buffalo.cse562.model.operatorabstract.UnaryOperator;

public class OrderByOperator extends UnaryOperator{
	
	List<Expression> orderByExpression = null;
	
	public OrderByOperator(List<Expression> orderByExpression) {
		this.orderByExpression = orderByExpression;
	}
	@Override
	public void dataIn() {
		
	}

	@Override
	public void dataOut() {
		
	}
}
