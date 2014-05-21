package edu.buffalo.cse562.visitor;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.ArrayList;
import java.util.List;


public class InsertItemsListVisitor implements ItemsListVisitor {
    List<String> newTuples;

    public InsertItemsListVisitor() {
        newTuples = new ArrayList<>();
    }

    @Override
    public void visit(SubSelect subSelect) {
        throw new UnsupportedOperationException("Subselect not supported in Insert");
    }

    @Override
    public void visit(ExpressionList expressionList) {
        List<Expression> expressions = expressionList.getExpressions();
        StringValueExtractor stringValueExtractor = new StringValueExtractor();
        StringBuilder sb = new StringBuilder();
        String value;

        for (Expression expression : expressions) {
            expression.accept(stringValueExtractor);
            value = stringValueExtractor.getValue();
            sb.append("|").append(value);
        }
        newTuples.add(sb.substring(1));
    }

    public List<String> getNewTuples() {
        return newTuples;
    }
}
