package edu.buffalo.cse562.visitor.optimizer;

import edu.buffalo.cse562.operator.JoinOperator;
import edu.buffalo.cse562.operator.Operator;
import edu.buffalo.cse562.operator.SelectionOperator;
import javafx.util.Pair;
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
        List<Pair<Integer, Operator>> operatorPriorityPairList = convertScanToSelectionOperators();
        sortInDecreasingOrderOrNoOfConditions(operatorPriorityPairList);
        return chainSelectionIntoAJoinOperator(operatorPriorityPairList);
    }

    private Operator chainSelectionIntoAJoinOperator(List<Pair<Integer, Operator>> operatorPriorityPairList) {
        Iterator<Pair<Integer, Operator>> iterator = operatorPriorityPairList.iterator();
        Operator result = iterator.next().getValue();
        Operator o;
        do {
            o = iterator.next().getValue();
            result = new JoinOperator(result, o);
        } while (iterator.hasNext());
        return result;
    }

    private List<Pair<Integer, Operator>> convertScanToSelectionOperators() {
        List<Pair<Integer, Operator>> operatorPriorityPairList = new ArrayList<>();
        Operator hybridOperator;
        Integer weightage;
        for (Operator inputOperator : inputOperators) {
            List<Expression> exclusiveConditions = optimizer.getListOfConditionsExclusiveToThisTable(inputOperator.getSchema());

            hybridOperator = getChainedSelectionOperator(inputOperator, exclusiveConditions);
            weightage = exclusiveConditions.size();

            operatorPriorityPairList.add(new Pair<>(weightage, hybridOperator));
        }
        return operatorPriorityPairList;
    }

    private void sortInDecreasingOrderOrNoOfConditions(List<Pair<Integer, Operator>> operatorPriorityPairList) {
        Collections.sort(operatorPriorityPairList, new Comparator<Pair<Integer, Operator>>() {
            @Override
            public int compare(Pair<Integer, Operator> p1, Pair<Integer, Operator> p2) {
                return p2.getKey().compareTo(p1.getKey());
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
