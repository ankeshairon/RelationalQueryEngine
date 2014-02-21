package edu.buffalo.cse562.model.operators;

import edu.buffalo.cse562.model.data.ResultSet;
import edu.buffalo.cse562.model.operatorabstract.UnaryOperator;
import net.sf.jsqlparser.expression.Expression;

import java.util.List;

public class OrderByOperator implements UnaryOperator {

    List<Expression> orderByExpression = null;
	
	public OrderByOperator(List<Expression> orderByExpression) {
		this.orderByExpression = orderByExpression;
	}

    @Override
    public ResultSet dataOut() {
        return null;
    }

    @Override
    public void dataIn(ResultSet[] data) {

    }
}
