package edu.buffalo.cse562.model.operators;

import edu.buffalo.cse562.mock.Datum;
import edu.buffalo.cse562.model.operatorabstract.UnaryOperator;
import net.sf.jsqlparser.expression.Expression;

import java.util.List;

public class OrderByOperator extends UnaryOperator{
	
	List<Expression> orderByExpression = null;
	
	public OrderByOperator(List<Expression> orderByExpression) {
		this.orderByExpression = orderByExpression;
	}

    @Override
    public void dataIn(Datum data) {
        super.dataIn(data);
    }

    @Override
    public Datum dataOut() {
        return super.dataOut();
    }
}
