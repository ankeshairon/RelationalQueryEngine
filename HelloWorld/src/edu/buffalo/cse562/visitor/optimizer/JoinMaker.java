package edu.buffalo.cse562.visitor.optimizer;

import edu.buffalo.cse562.operator.JoinOperator;
import edu.buffalo.cse562.operator.Operator;
import edu.buffalo.cse562.operator.SelectionOperator;
import net.sf.jsqlparser.expression.Expression;

import java.util.*;

public class JoinMaker {
    private final List<Operator> inputOperators;
    private CrossToJoinOptimizer optimizer;


    public JoinMaker(Expression where, List<Operator> inputOperators) {
        this.inputOperators = inputOperators;
        optimizer = new CrossToJoinOptimizer(where);
    }


    public Operator getOptimizedChainedJoinOperator() {
        List<Map.Entry<Integer, Operator>> operatorPriorityPairList = convertScanToSelectionOperators();
        sortInDecreasingOrderOrNoOfConditions(operatorPriorityPairList);
        return chainSelectionIntoAJoinOperator(operatorPriorityPairList);
    }

    private Operator chainSelectionIntoAJoinOperator(List<Map.Entry<Integer, Operator>> operatorPriorityPairList) {
        Iterator<Map.Entry<Integer, Operator>> iterator = operatorPriorityPairList.iterator();
        Operator result = iterator.next().getValue();
        Operator o;
        do {
            o = iterator.next().getValue();
            result = new JoinOperator(result, o);
        } while (iterator.hasNext());
        return result;
    }

    private List<Map.Entry<Integer, Operator>> convertScanToSelectionOperators() {
        List<Map.Entry<Integer, Operator>> operatorPriorityPairList = new ArrayList<>();
        Operator hybridOperator;
        Integer weightage;
        for (Operator inputOperator : inputOperators) {
            List<Expression> exclusiveConditions = optimizer.getListOfConditionsExclusiveToThisTable(inputOperator.getSchema());

            hybridOperator = getChainedSelectionOperator(inputOperator, exclusiveConditions);
            weightage = exclusiveConditions.size();

            operatorPriorityPairList.add(new HashMap.SimpleEntry<>(weightage, hybridOperator));
        }
        return operatorPriorityPairList;
    }

    private void sortInDecreasingOrderOrNoOfConditions(List<Map.Entry<Integer, Operator>> operatorPriorityPairList) {
        Collections.sort(operatorPriorityPairList, new Comparator<Map.Entry<Integer, Operator>>() {
            @Override
            public int compare(Map.Entry<Integer, Operator> e1, Map.Entry<Integer, Operator> e2) {
                return e2.getKey().compareTo(e1.getKey());
            }
        });
    }

    //todo replace multiple selection operators with one selection operator with multiple conditions
    private Operator getChainedSelectionOperator(Operator oldSource, List<Expression> exclusiveConditionalExpressions) {
        Operator newSource = oldSource;

        for (Expression exclusiveConditionalExpression : exclusiveConditionalExpressions) {
            newSource = new SelectionOperator(oldSource, exclusiveConditionalExpression);
            oldSource = newSource;
        }


        return newSource;
    }

}
