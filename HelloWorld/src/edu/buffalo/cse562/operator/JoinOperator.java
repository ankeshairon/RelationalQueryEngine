package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.visitor.CrossToJoinOptimizationEvaluator;
import edu.buffalo.cse562.visitor.optimizer.CrossToJoinOptimizer;
import net.sf.jsqlparser.expression.Expression;

import java.util.Iterator;
import java.util.List;

public class JoinOperator implements Operator {
    Operator input1;
    Operator input2;
    Datum[] temp1;
    Datum[] temp2;
    ColumnSchema[] schema;

    public JoinOperator(Operator input1, Operator input2, Expression where) {
        assignInputs(input1, input2, where);
        updateSchema();
        temp1 = new Datum[input1.getSchema().length];
        temp2 = new Datum[input2.getSchema().length];
        temp1 = input1.readOneTuple();
        //temp2 = input2.readOneTuple();
    }

    private void assignInputs(Operator in1, Operator in2, Expression where) {
        CrossToJoinOptimizationEvaluator optimizationEvaluator = new CrossToJoinOptimizationEvaluator(where);
        CrossToJoinOptimizer optimizer = new CrossToJoinOptimizer(optimizationEvaluator.getConditionColumnMap());

        List<Expression> exclusiveConditionalExpressionsOfInput1 = optimizer.canPullASelectInCrossToMakeAJoin(in1.getSchema());
        List<Expression> exclusiveConditionalExpressionsOfInput2 = optimizer.canPullASelectInCrossToMakeAJoin(in2.getSchema());

        if (!exclusiveConditionalExpressionsOfInput1.isEmpty()) {
            input1 = chainInSelectionOperators(in1, exclusiveConditionalExpressionsOfInput1);
            input2 = in2;
        } else if (!exclusiveConditionalExpressionsOfInput2.isEmpty()) {
            input1 = chainInSelectionOperators(in2, exclusiveConditionalExpressionsOfInput2);
            input2 = in1;
        } else {
            input1 = in1;
            input2 = in2;
        }

    }

    //todo replace multiple selection operators with one selection operator with multiple conditions
    private Operator chainInSelectionOperators(Operator oldSource, List<Expression> exclusiveConditionalExpressions) {
        Operator newSource;
        Iterator<Expression> iterator = exclusiveConditionalExpressions.iterator();

        do {
            newSource = new SelectionOperator(oldSource, iterator.next());
            oldSource = newSource;
        } while (iterator.hasNext());

        return newSource;
    }

    public void updateSchema() {
        int size1 = input1.getSchema().length;
        int size2 = input2.getSchema().length;
        schema = new ColumnSchema[size1 + size2];
        int i = 0;
        for (ColumnSchema cs : input1.getSchema()) {
            schema[i] = cs;
            i++;
        }
        for (ColumnSchema cs : input2.getSchema()) {
            schema[i] = cs;
            i++;
        }
    }

    @Override
    public Datum[] readOneTuple() {
        Datum[] ret = new Datum[schema.length];


        while (temp1 != null) {
            while ((temp2 = input2.readOneTuple()) != null) {
                int counter = 0;
                for (int i = 0; i < temp1.length; i++) {
                    ret[counter] = temp1[i];
                    counter++;
                }
                for (int i = 0; i < temp2.length; i++) {
                    ret[counter] = temp2[i];
                    counter++;
                }
                //temp2 = input2.readOneTuple();
                return ret;
            }
            temp1 = input1.readOneTuple();
            input2.reset();
        }

        return null;
    }

    @Override
    public void reset() {
        input1.reset();
        input2.reset();
        temp1 = input1.readOneTuple();
    }

    @Override
    public ColumnSchema[] getSchema() {
        return schema;
    }

}
