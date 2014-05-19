package edu.buffalo.cse562.visitor;

import edu.buffalo.cse562.indexer.service.IndexService;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.List;


public class InsertItemsListVisitor implements ItemsListVisitor {
    Table table;

    public InsertItemsListVisitor(Table table) {
        this.table = table;
    }

    @Override
    public void visit(SubSelect subSelect) {
        throw new UnsupportedOperationException("Subselect not supported in Insert");
    }

    @Override
    public void visit(ExpressionList expressionList) {
        List<Expression> expressions = expressionList.getExpressions();
        StringBuilder sb = new StringBuilder();
        InsertItemVisitor evalVisitor = new InsertItemVisitor();

        for (Expression expression : expressions) {
            expression.accept(evalVisitor);
            String value = evalVisitor.getKey();
            sb.append("|").append(value);
        }
        IndexService.getInstance().addTupleToTable(table.getName(), sb.toString().substring(1, sb.length()));
    }
}
