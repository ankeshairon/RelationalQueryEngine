package edu.buffalo.cse562.visitor.optimizer;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.operator.HybridHashJoinOperator;
import edu.buffalo.cse562.operator.NestedLoopJoinOperator;
import edu.buffalo.cse562.operator.Operator;
import edu.buffalo.cse562.operator.SelectionOperator;
import net.sf.jsqlparser.expression.Expression;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class JoinMaker {
    private final List<Operator> inputOperators;
    private final File swapDir;
    private CrossToJoinOptimizer optimizer;


    public JoinMaker(Expression where, List<Operator> inputOperators, File swapDir) {
        this.inputOperators = inputOperators;
        this.swapDir = swapDir;
        optimizer = new CrossToJoinOptimizer(where);
    }


    public Operator getOptimizedChainedJoinOperator() {
        List<Map.Entry<Long, Operator>> operatorPriorityPairList = convertScanToSelectionOperators();
        sortInDecreasingOrderOfDataSize(operatorPriorityPairList);
//        sortInDecreasingOrderOrNoOfConditions(operatorPriorityPairList);
        return chainSelectionIntoAJoinOperator(operatorPriorityPairList);
    }

    private Operator chainSelectionIntoAJoinOperator(List<Map.Entry<Long, Operator>> operatorPriorityPairList) {
        Iterator<Map.Entry<Long, Operator>> iterator = operatorPriorityPairList.iterator();
        Operator chainedOperator = iterator.next().getValue();
        Operator o;
        do {
            o = iterator.next().getValue();
            chainedOperator = getJoinOperator(chainedOperator, o);
        } while (iterator.hasNext());
        return chainedOperator;
    }

    private List<Map.Entry<Long, Operator>> convertScanToSelectionOperators() {
        List<Map.Entry<Long, Operator>> operatorPriorityPairList = new ArrayList<>();
        Operator hybridOperator;
        Long size;
//        int size;
        for (Operator inputOperator : inputOperators) {
            List<Expression> exclusiveConditions = optimizer.getConditionsExclusiveToTable(inputOperator.getSchema());
            if (exclusiveConditions.isEmpty()) {
                hybridOperator = inputOperator;
            } else {
                //todo instantiate index scan operator instead
                hybridOperator = new SelectionOperator(inputOperator, exclusiveConditions);
            }
//            size = exclusiveConditions.size();
            size = hybridOperator.getProbableTableSize();
            operatorPriorityPairList.add(new HashMap.SimpleEntry<>(size, hybridOperator));
        }
        return operatorPriorityPairList;
    }

    private void sortInDecreasingOrderOfDataSize(List<Map.Entry<Long, Operator>> operatorPriorityPairList) {
        Collections.sort(operatorPriorityPairList, new Comparator<Map.Entry<Long, Operator>>() {
            @Override
            public int compare(Map.Entry<Long, Operator> e1, Map.Entry<Long, Operator> e2) {
                return e2.getKey().compareTo(e1.getKey());
            }
        });
    }

    /*private void sortInDecreasingOrderOrNoOfConditions(List<Map.Entry<Long, SelectionOperator>> operatorPriorityPairList) {
        Collections.sort(operatorPriorityPairList, new Comparator<Map.Entry<Long, SelectionOperator>>() {
            @Override
            public int compare(Map.Entry<Long, SelectionOperator> e1, Map.Entry<Long, SelectionOperator> e2) {
                return e2.getKey().compareTo(e1.getKey());
            }
        });
    }*/

    private Operator getJoinOperator(Operator chainedOperator, Operator nextOperator) {
        Integer[] indexes = optimizer.getIndexesOfJoinColumns(chainedOperator.getSchema(), nextOperator.getSchema());

        if (indexes != null) {
            try {
                return new HybridHashJoinOperator(chainedOperator, nextOperator, indexes[0], indexes[1], swapDir);
            } catch (IOException | Datum.CastException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return new NestedLoopJoinOperator(chainedOperator, nextOperator);
        }
    }

    public List<Expression> getNonExclusiveConditionClauses() {
        List<Expression> nonExclusiveConditionClauses = new ArrayList<>();
        nonExclusiveConditionClauses.addAll(optimizer.getNonExclusiveConditions().keySet());
        return nonExclusiveConditionClauses;
    }
}
