package edu.buffalo.cse562.visitor.optimizer;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.operator.HybridHashJoinOperator;
import edu.buffalo.cse562.operator.Operator;
import edu.buffalo.cse562.operator.SelectionOperator;
import net.sf.jsqlparser.expression.Expression;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
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
        List<Map.Entry<Long, SelectionOperator>> operatorPriorityPairList = convertScanToSelectionOperators();
        sortInIncreasingOrderOfDataSize(operatorPriorityPairList);
//        sortInDecreasingOrderOrNoOfConditions(operatorPriorityPairList);
        return chainSelectionIntoAJoinOperator(operatorPriorityPairList);
    }

    private Operator chainSelectionIntoAJoinOperator(List<Map.Entry<Long, SelectionOperator>> operatorPriorityPairList) {
        Iterator<Map.Entry<Long, SelectionOperator>> iterator = operatorPriorityPairList.iterator();
        Operator chainedOperator = iterator.next().getValue();
        Operator o;
        do {
            o = iterator.next().getValue();
            chainedOperator = getJoinOperator(chainedOperator, o);
        } while (iterator.hasNext());
        return chainedOperator;
    }

    private List<Map.Entry<Long, SelectionOperator>> convertScanToSelectionOperators() {
        List<Map.Entry<Long, SelectionOperator>> operatorPriorityPairList = new ArrayList<>();
        SelectionOperator hybridOperator;
        Long size;
        for (Operator inputOperator : inputOperators) {
            List<Expression> exclusiveConditions = optimizer.getConditionsExclusiveToTable(inputOperator.getSchema());

            hybridOperator = new SelectionOperator(inputOperator, exclusiveConditions);
//            size = exclusiveConditions.size();
            size = hybridOperator.getTableSize();
            operatorPriorityPairList.add(new HashMap.SimpleEntry<>(size, hybridOperator));
        }
        return operatorPriorityPairList;
    }

    private void sortInIncreasingOrderOfDataSize(List<Map.Entry<Long, SelectionOperator>> operatorPriorityPairList) {
        Collections.sort(operatorPriorityPairList, new Comparator<Map.Entry<Long, SelectionOperator>>() {
            @Override
            public int compare(Map.Entry<Long, SelectionOperator> e1, Map.Entry<Long, SelectionOperator> e2) {
                return e1.getKey().compareTo(e2.getKey());
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
//        return new NestedLoopJoinOperator(chainedOperator, nextOperator);

        Integer[] indexes = optimizer.getIndexesOfJoinColumns(chainedOperator.getSchema(), nextOperator.getSchema());
        try {
            return new HybridHashJoinOperator(chainedOperator, nextOperator, indexes[0], indexes[1], swapDir);
        } catch (IOException | Datum.CastException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Expression> getNonExclusiveConditionClauses() {
        List<Expression> nonExclusiveConditionClauses = new ArrayList<>();
        nonExclusiveConditionClauses.addAll(optimizer.getNonExclusiveConditions().keySet());
        return nonExclusiveConditionClauses;
    }
}
