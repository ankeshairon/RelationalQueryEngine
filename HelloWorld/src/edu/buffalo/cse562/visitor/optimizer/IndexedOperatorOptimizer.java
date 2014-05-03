package edu.buffalo.cse562.visitor.optimizer;

import edu.buffalo.cse562.operator.Operator;
import edu.buffalo.cse562.operator.SelectionOperator;
import edu.buffalo.cse562.operator.indexscan.IndexScanOperator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IndexedOperatorOptimizer {

    public Operator getHybridOperator(Operator inputOperator, Map<Expression, List<Column>> exclusiveConditionsColumnMap) {
        Operator resultantOperator = inputOperator;

        final List<Expression> conditionsForSelectionOperator = new ArrayList<>();
        final List<Expression> conditionsForIndexScanOperator = new ArrayList<>();
        identifyConditionsToBePushedIn(exclusiveConditionsColumnMap, conditionsForSelectionOperator, conditionsForIndexScanOperator);

        if (conditionsForIndexScanOperator.size() != 0) {
            resultantOperator = new IndexScanOperator(inputOperator, conditionsForIndexScanOperator);
        }
        if (conditionsForSelectionOperator.size() != 0) {
            resultantOperator = new SelectionOperator(resultantOperator, conditionsForSelectionOperator);
        }

        return resultantOperator;

    }

    private void identifyConditionsToBePushedIn(Map<Expression, List<Column>> exclusiveConditionsColumnMap, List<Expression> conditionsForSelectionOperator, List<Expression> conditionsForIndexScanOperator) {
        for (Expression condition : exclusiveConditionsColumnMap.keySet()) {
            if (false && conditionsForIndexScanOperator.size() != 1 && conditionCanBeUsedInIndexScan(condition, exclusiveConditionsColumnMap.get(condition).size())) {
                conditionsForIndexScanOperator.add(condition);
            } else {
                conditionsForSelectionOperator.add(condition);
            }
        }
    }

    //todo add support if expression is OR (and NOT AND) with only single column inside
    private boolean conditionCanBeUsedInIndexScan(Expression condition, int noOfColumnsUsed) {
        final String tabooForIndexScan1 = " or ";
        final String tabooForIndexScan2 = " OR ";
        if (condition.toString().contains(tabooForIndexScan1) || condition.toString().contains(tabooForIndexScan2) || (noOfColumnsUsed != 1)) {
            return false;
        } else {
            return true;
        }
    }
}
