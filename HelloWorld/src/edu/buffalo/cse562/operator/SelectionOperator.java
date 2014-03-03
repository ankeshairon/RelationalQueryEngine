package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.visitor.EvaluatorSelection;
import net.sf.jsqlparser.expression.Expression;

public class SelectionOperator implements Operator {

    Operator input;
    Expression condition;

    public SelectionOperator(Operator input, Expression condition) {
        this.input = input;
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

            //todo modify logic to support one time instantiation
            EvaluatorSelection eval = new EvaluatorSelection(input.getSchema(), tuple);
            condition.accept(eval);
            try {
                eval.executeStack();
            } catch (Datum.CastException e) {
                e.printStackTrace();
            }

            if (!eval.getBool()) {
                tuple = null;
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
        return input.getSchema();
    }

}
