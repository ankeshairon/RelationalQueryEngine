package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.operator.abstractoperators.FilterOperator;
import edu.buffalo.cse562.operator.abstractoperators.Operator;
import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.visitor.EvaluatorSelection;
import net.sf.jsqlparser.expression.Expression;

import java.util.ArrayList;
import java.util.List;

public class SelectionOperator implements FilterOperator {
    private List<Expression> conditions;

    Operator input;
    EvaluatorSelection evaluatorSelection;

    public SelectionOperator(Operator input, List<Expression> conditions) {
        this.input = input;
        this.conditions = conditions;
        evaluatorSelection = new EvaluatorSelection(input.getSchema(), conditions);
    }

    @Override
    public Datum[] readOneTuple() {
        Datum[] tuple;
        do {
            tuple = input.readOneTuple();
            if (tuple == null) {
                return null;
            }

            try {
                evaluatorSelection.executeStack(tuple);
            } catch (Datum.CastException e) {
                e.printStackTrace();
            }

            if (!evaluatorSelection.getResult()) {
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

    public Long getProbableTableSize() {
        return input.getProbableTableSize();
    }

    @Override
    public List<Expression> getConditions() {
        List<Expression> allConditions = new ArrayList<>();
        allConditions.addAll(conditions);
        if (input instanceof IndexScanOperator) {
            allConditions.addAll(((IndexScanOperator) input).getConditions());
        }
        return allConditions;
    }

    @Override
    public List<Integer> getRelevantColumnIndexes() {
        return ((FilterOperator) input).getRelevantColumnIndexes();
    }
}
