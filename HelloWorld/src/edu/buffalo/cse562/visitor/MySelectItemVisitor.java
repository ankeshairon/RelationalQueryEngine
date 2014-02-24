package edu.buffalo.cse562.visitor;

import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;

public class MySelectItemVisitor implements SelectItemVisitor {

    public ColumnSchema[] inSchema;
    public ColumnSchema[] outSchema;

    int counter;
    int[] indexes;

    public MySelectItemVisitor(ColumnSchema[] inSchema, ColumnSchema[] outSchema, int[] indexes) {
        this.inSchema = inSchema;
        this.outSchema = outSchema;
        this.indexes = indexes;
        counter = 0;
    }

    public int[] getIndexes() {
        return indexes;
    }

    public ColumnSchema[] getOutSchema() {
        return outSchema;
    }

    @Override
    public void visit(AllColumns allColumns) {
        outSchema = new ColumnSchema[inSchema.length];
        indexes = new int[inSchema.length];
        for (int i = 0; i < inSchema.length; i++) {
            indexes[i] = i;
        }
        outSchema = inSchema;
        //counter++;
    }

    @Override
    public void visit(AllTableColumns atblcol) {
        int nofcols = 0;
        for (int i = 0; i < inSchema.length; i++) {
            if (inSchema[i].tblName.equalsIgnoreCase(atblcol.getTable().getName())) {
                nofcols++;
            }
        }
        outSchema = new ColumnSchema[nofcols];
        indexes = new int[nofcols];
        for (int i = 0; i < inSchema.length; i++) {
            if (inSchema[i].tblName.equalsIgnoreCase(atblcol.getTable().getName())) {
                outSchema[counter] = inSchema[i];
                indexes[counter] = i;
                counter++;
            }
        }
    }

    @Override
    public void visit(SelectExpressionItem arg0) {
        counter++;
        /* To create a executable stack of the expression
         * Tuple data is not available here so I cannot calculate the needed value
         */
        
        Expression projectExpression = arg0.getExpression();
        String projectAlias = arg0.getAlias();
        
        EvaluatorProjection evaluatorProjection = new EvaluatorProjection(projectExpression,projectAlias);
        
    }

}
