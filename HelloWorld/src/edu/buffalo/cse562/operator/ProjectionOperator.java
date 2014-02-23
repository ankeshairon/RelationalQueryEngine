package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.visitor.MySelectItemVisitor;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.List;

public class ProjectionOperator implements Operator {

    Operator input;
    ColumnSchema[] inputSchema;
    ColumnSchema[] outputSchema;
    List<SelectItem> selItems;
    int[] indexes;

    public ProjectionOperator(Operator in, List<SelectItem> it) {
        this.input = in;
        this.selItems = it;
        this.inputSchema = input.getSchema();
        updateSchema();
    }

    public void updateSchema() {
        //outputSchema = new ColumnSchema[selItems.size()];
        MySelectItemVisitor mySelVisitor = new MySelectItemVisitor(inputSchema, outputSchema, indexes);
        for (SelectItem item : selItems) {
            item.accept(mySelVisitor);
        }
        outputSchema = mySelVisitor.getOutSchema();
        indexes = mySelVisitor.getIndexes();
    }

    @Override
    public Datum[] readOneTuple() {
        Datum[] ret = new Datum[outputSchema.length];
        Datum[] tuple;
        if ((tuple = input.readOneTuple()) != null) {
            for (int i = 0; i < indexes.length; i++) {
                ret[i] = tuple[indexes[i]];
            }
            return ret;
        }
        return null;
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public ColumnSchema[] getSchema() {
        // TODO Auto-generated method stub
        return null;
    }

}
