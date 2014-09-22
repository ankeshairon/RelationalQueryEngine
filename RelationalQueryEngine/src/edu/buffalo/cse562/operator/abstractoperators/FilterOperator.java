package edu.buffalo.cse562.operator.abstractoperators;

import net.sf.jsqlparser.expression.Expression;

import java.util.List;

public interface FilterOperator extends Operator {
    public List<Integer> getRelevantColumnIndexes();

    public List<Expression> getConditions();
}
