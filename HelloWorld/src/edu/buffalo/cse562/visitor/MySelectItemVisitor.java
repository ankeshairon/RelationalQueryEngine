package edu.buffalo.cse562.visitor;

import edu.buffalo.cse562.operator.Operator;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MySelectItemVisitor implements SelectItemVisitor {

    Operator in;
    private Boolean isAggregationPresent;

    ColumnSchema[] inputSchema;
    List<ColumnSchema> outputSchema;
    List<Integer> indexes;
    EvaluatorProjection evalProjection;

    public MySelectItemVisitor(Operator in) {
        this.in = in;
        inputSchema = in.getSchema();
        outputSchema = new ArrayList<>();
        indexes = new ArrayList<>();
        evalProjection = new EvaluatorProjection(inputSchema, outputSchema, indexes);
        isAggregationPresent = false;
    }

    @Override
    public void visit(AllColumns allColumns) {
        for (int i = 0; i < inputSchema.length; i++) {
            indexes.add(i);
        }
        outputSchema = Arrays.asList(inputSchema);
    }

    @Override
    public void visit(AllTableColumns allTableColumns) {
    }

    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {
        Expression expr = selectExpressionItem.getExpression();
        String alias = selectExpressionItem.getAlias();
        evalProjection = new EvaluatorProjection(expr, alias);
        expr.accept(evalProjection);
        if (evalProjection.isAnAggregation()) {
            isAggregationPresent = true;
        }
     /*
        if (expr instanceof Column) {
            for (int i = 0; i < inputSchema.length; i++) {
                if (((Column) expr).getColumnName().equalsIgnoreCase(inputSchema[i].getColName())) {
                    indexes.add(i);
                    ColumnSchema columnSchema = new ColumnSchema(inputSchema[i].getColName(), inputSchema[i].getType());
                    columnSchema.setAlias(inputSchema[i].getAlias());
                    outputSchema.add(columnSchema);
                    counter++;
                    break;
                }
            }
        } else {
            indexes.set(counter, -1);
            //if(selectExpressionItem.getAlias() == null ){
            outputSchema.get(counter).setColName(expr.toString());
            outputSchema.get(counter).setAlias(expr.toString());
            outputSchema.get(counter).setType(Datum.type.FLOAT);
            counter++;
        }
    */
    }

    public boolean isAggregationPresent() {
        return isAggregationPresent;
    }
}
