package edu.buffalo.cse562.visitor.optimizer;

import edu.buffalo.cse562.operator.Operator;
import edu.buffalo.cse562.operator.SelectionOperator;
import edu.buffalo.cse562.operator.indexscan.IndexScanOperator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

import java.util.*;

public class IndexedOperatorOptimizer {

    public Operator getHybridOperator(Operator inputOperator, Map<Expression, List<Column>> exclusiveConditionsColumnMap) {
        Operator resultantOperator = inputOperator;

        final List<Expression> conditionsForSelectionOperator = new ArrayList<>();
        final List<Expression> conditionsForIndexScanOperator = new ArrayList<>();
        identifyConditionsForOperators(exclusiveConditionsColumnMap, conditionsForSelectionOperator, conditionsForIndexScanOperator);

        if (conditionsForIndexScanOperator.size() != 0) {
            resultantOperator = new IndexScanOperator(inputOperator, conditionsForIndexScanOperator);
        }
        if (conditionsForSelectionOperator.size() != 0) {
            resultantOperator = new SelectionOperator(resultantOperator, conditionsForSelectionOperator);
        }
        return resultantOperator;
    }

    private void identifyConditionsForOperators(Map<Expression, List<Column>> exclusiveConditionsColumnMap,
                                                List<Expression> conditionsForSelectionOperator,
                                                List<Expression> conditionsForIndexScanOperator) {

        List<Map.Entry<Expression,Integer>> conditionWeights = createConditionWeights(exclusiveConditionsColumnMap);
        final Iterator<Map.Entry<Expression,Integer>> iterator = conditionWeights.iterator();

        if (iterator.hasNext()) {
            conditionsForIndexScanOperator.add(iterator.next().getKey());
        }
        while (iterator.hasNext()) {
            conditionsForSelectionOperator.add(iterator.next().getKey());
        }
    }

    private List<Map.Entry<Expression,Integer>> createConditionWeights(Map<Expression, List<Column>> exclusiveConditionsColumnMap) {
        LinkedHashMap<Expression, Integer> conditionWeights = new LinkedHashMap<>();
        Expression condition;
        List<Column> columns;
        Integer weightOffset;

        for (Map.Entry<Expression, List<Column>> conditionColumnsEntry : exclusiveConditionsColumnMap.entrySet()) {
            condition = conditionColumnsEntry.getKey();
            columns = conditionColumnsEntry.getValue();
            weightOffset = calculateWeightOffset(condition.toString(), columns);

            if (conditionWeights.get(condition) == null) {
                conditionWeights.put(condition, weightOffset);
            } else {
                conditionWeights.put(condition, conditionWeights.get(condition) + weightOffset);
            }
        }

        List<Map.Entry<Expression, Integer>> priorityMap = new ArrayList<>();
        priorityMap.addAll(conditionWeights.entrySet());

        Collections.sort(priorityMap, new Comparator<Map.Entry<Expression, Integer>>() {
            @Override
            public int compare(Map.Entry<Expression, Integer> o1, Map.Entry<Expression, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        return priorityMap;
    }

    private Integer calculateWeightOffset(String condition, List<Column> columns) {
        if (condition.contains(" or ") || condition.contains(" OR ") || (columns.size() != 1)) {
            return Integer.MIN_VALUE; //coz not supported
        }

//        final Column column = columns.get(0);
        Integer offset = 0;
//        if (column.getColumnName().contains("key")) {
//            offset = offset + 20;
//        }
//        if (condition.contains("<")) {
//            offset = offset - 20;
//        }
//        if (condition.contains(">")) {
//            offset = offset - 20;
//        }

        return offset;
    }

    //todo identify which condition is best
    //todo add support if expression is OR (and NOT AND) with only single column inside

}
