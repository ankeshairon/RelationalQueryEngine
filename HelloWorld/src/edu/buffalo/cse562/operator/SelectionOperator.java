package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.visitor.EvaluatorSelection;
import net.sf.jsqlparser.expression.Expression;

public class SelectionOperator implements Operator {

    Operator input;
    ColumnSchema[] schema;
    Expression condition;

    public SelectionOperator(Operator input, ColumnSchema[] schema, Expression condition) {
        this.input = input;
        this.schema = schema;
        this.condition = condition;
    }

    @Override
    public Datum[] readOneTuple() {
        Datum[] tuple;
        do {
            tuple = input.readOneTuple();
            if (tuple == null) {
                return null;
            }

            if (condition != null) {
                EvaluatorSelection eval = new EvaluatorSelection(schema, tuple);
                condition.accept(eval);
                try {
            	eval.executeStack();
                } catch (Datum.CastException e) {
                    e.printStackTrace();
                }

                if (!eval.getBool()) {
                    tuple = null;
                }
            }

        } while (tuple == null);
        return tuple;
    }

    @Override
    public void reset() {
        input.reset();
    }

    @Override
    public ColumnSchema[] getSchema() {
        return schema;
    }

}
