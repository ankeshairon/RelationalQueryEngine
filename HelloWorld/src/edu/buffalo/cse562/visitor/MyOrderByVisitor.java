package edu.buffalo.cse562.visitor;

import edu.buffalo.cse562.operator.Operator;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.OrderByVisitor;

import java.util.LinkedHashMap;

public class MyOrderByVisitor implements OrderByVisitor {

    private Operator in;
    LinkedHashMap<Integer, Boolean> indexesOfColumnsToSortOn;

    public MyOrderByVisitor(Operator in) {
        this.in = in;
        indexesOfColumnsToSortOn = new LinkedHashMap<>();
    }

    @Override
    public void visit(OrderByElement orderBy) {
        ColumnSchema[] inSchema = in.getSchema();
        final String orderByColumnName = orderBy.getExpression().toString();

        for (int i = 0; i < inSchema.length; i++) {
            if (inSchema[i].matchColumn(orderByColumnName)) {
                indexesOfColumnsToSortOn.put(i, orderBy.isAsc());
                break;
            }
        }

    }
}
