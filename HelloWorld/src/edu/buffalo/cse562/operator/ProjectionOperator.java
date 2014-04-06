package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.visitor.EvaluatorAggregate;
import net.sf.jsqlparser.expression.Expression;

public class ProjectionOperator implements Operator {

    Operator input;
    ColumnSchema[] outputSchema;
    Integer[] indexes;

    public ProjectionOperator(Operator in, ColumnSchema[] outputSchema, Integer[] indexes) {
        input = in;
        this.outputSchema = outputSchema;
        this.indexes = indexes;
    }

    @Override
    public Datum[] readOneTuple() {
        Datum[] ret = new Datum[outputSchema.length];
        Datum[] tuple;
        if ((tuple = input.readOneTuple()) != null) {
            for (int i = 0; i < indexes.length; i++) {
                if (indexes[i] >= 0) {
                    ret[i] = tuple[indexes[i]];
                } else {
                    ret[i] = evaluateExpression(tuple, input.getSchema(), outputSchema[i].getExpression());
                }
            }
            return ret;
        }
        return null;
    }

    @Override
    public void reset() {
        input.reset();
    }

    @Override
    public ColumnSchema[] getSchema() {
        return outputSchema;
    }

    private Datum evaluateExpression(Datum[] oldDatum, ColumnSchema[] oldSchema, Expression expression) {
        EvaluatorAggregate evalAggregate = new EvaluatorAggregate(oldDatum, oldSchema, expression);
//        expression.accept(evalAggregate);
        Datum floatDatum = null;
        try {
            floatDatum = evalAggregate.executeStack();
        } catch (Datum.CastException e) {
            e.printStackTrace();
        }
        return floatDatum;
    }
}