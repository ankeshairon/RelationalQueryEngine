package edu.buffalo.cse562.visitor;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.operator.Operator;
import edu.buffalo.cse562.operator.ProjectionOperator;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;

public class MySelectItemVisitor implements SelectItemVisitor {

    Operator source;
    Operator in;
    private int noOfSelectItems;
    int counter = 0;


    public MySelectItemVisitor(Operator in, int noOfSelectItems) {
        this.in = in;
        this.noOfSelectItems = noOfSelectItems;
    }


    @Override
    public void visit(AllColumns allColumns) {
        int[] indexes = new int[in.getSchema().length];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }
        source = new ProjectionOperator(in, in.getSchema(), indexes);
    }

    @Override
    public void visit(AllTableColumns allTableColumns) {
    }

    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {
        Expression expr = selectExpressionItem.getExpression();
        ColumnSchema[] inputSchema = in.getSchema();
        ColumnSchema[] outputSchema = new ColumnSchema[noOfSelectItems];
        int[] indexes = new int[noOfSelectItems];

        if (expr instanceof Column) {
            for (int i = 0; i < inputSchema.length; i++) {
                if (((Column) expr).getColumnName().equalsIgnoreCase(inputSchema[i].getColName())) {
                    indexes[counter] = i;
                    outputSchema[counter] = new ColumnSchema(inputSchema[i].getColName(), inputSchema[i].getType());
                    outputSchema[counter].setAlias(inputSchema[i].getAlias());
                    counter++;
                }
            }
        } else {
            // todo for Ankesh
            indexes[counter] = -1;
//            if(selectExpressionItem.getAlias() == null ){
            outputSchema[counter].setColName(expr.toString());
            outputSchema[counter].setAlias(expr.toString());

//            } else{
//
//            }
            outputSchema[counter].setType(Datum.type.FLOAT);
            counter++;
        }

        source = new ProjectionOperator(in, outputSchema, indexes);
    }
}
