package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.operator.abstractoperators.Operator;
import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.visitor.EvaluatorAggregate;

public class ProjectionOperator implements Operator {

    Operator input;
    ColumnSchema[] outputSchema;
    Integer[] indexes;
    EvaluatorAggregate[] aggregateEvaluators;

    public ProjectionOperator(Operator in, ColumnSchema[] outputSchema, Integer[] indexes) {
        input = in;
        this.outputSchema = outputSchema;
        this.indexes = indexes;
        createEvaluatorsForAggregates();
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
                    ret[i] = aggregateEvaluators[i].executeStack(tuple);
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

    @Override
    public Long getProbableTableSize() {
        return input.getProbableTableSize();
    }

    private void createEvaluatorsForAggregates() {
        aggregateEvaluators = new EvaluatorAggregate[indexes.length];

        for (int i = 0; i < indexes.length; i++) {
            if (indexes[i] < 0) {
                aggregateEvaluators[i] = new EvaluatorAggregate(input.getSchema(), outputSchema[i].getExpression());
            }
        }
    }
}