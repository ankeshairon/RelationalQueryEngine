package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.data.Datum.CastException;
import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.visitor.Evaluator;
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
        Datum[] tuple = null;
        do {
            tuple = input.readOneTuple();
            if (tuple == null) {
                return null;
            }

            Evaluator eval = new Evaluator(schema, tuple);
            condition.accept(eval);

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
        // TODO Auto-generated method stub
        return null;
    }

}
