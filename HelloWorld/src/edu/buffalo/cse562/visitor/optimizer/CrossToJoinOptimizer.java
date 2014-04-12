package edu.buffalo.cse562.visitor.optimizer;

import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.visitor.CrossToJoinOptimizationEvaluator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CrossToJoinOptimizer {

    private Map<Expression, List<Column>> conditionColumnMap;

    public CrossToJoinOptimizer(Expression where) {
        conditionColumnMap = new CrossToJoinOptimizationEvaluator(where).getConditionColumnMap();
    }

    public List<Expression> getConditionsExclusiveToTable(ColumnSchema[] schema) {
        List<Expression> conditionalExpressions = new ArrayList<>();

        Iterator<Expression> iterator = conditionColumnMap.keySet().iterator();
        Expression condition;
        while (iterator.hasNext()) {
            condition = iterator.next();
            List<Column> columnsInConditionExpression = conditionColumnMap.get(condition);
            if (allColumnsForConditionOfThisTableOnly(columnsInConditionExpression, schema)) {
                conditionalExpressions.add(condition);
                iterator.remove();
            }
        }
        return conditionalExpressions;
    }

    public Integer[] getIndexesOfJoinColumns(ColumnSchema[] schema1, ColumnSchema[] schema2) {
        Iterator<Expression> iterator = conditionColumnMap.keySet().iterator();
        Expression condition;
        while (iterator.hasNext()) {
            condition = iterator.next();
            List<Column> columnsInConditionExpression = conditionColumnMap.get(condition);
            final Integer[] indexesOfBothTableColumnsForCondition = getIndexesOfBothTableColumnsForCondition(columnsInConditionExpression, schema1, schema2);
            if (indexesOfBothTableColumnsForCondition != null) {
                iterator.remove();
                return indexesOfBothTableColumnsForCondition;
            }
        }
        return null;
    }

    private Integer[] getIndexesOfBothTableColumnsForCondition(List<Column> columnsInConditionExpression, ColumnSchema[] schema1, ColumnSchema[] schema2) {
        Integer index1 = -1;
        Integer index2 = -1;
        for (int i = 0; i < columnsInConditionExpression.size(); i++) {
            if (indexOfColumnInConditionOfThisTable(columnsInConditionExpression.get(i), schema1) != -1) {
                index1 = i;
            } else if (indexOfColumnInConditionOfThisTable(columnsInConditionExpression.get(i), schema2) != -1) {
                index2 = i;
            } else {
                return null;
            }
        }
        return (index1 != -1 && index2 != -1) ? new Integer[]{index1, index2} : null;
    }


    private boolean allColumnsForConditionOfThisTableOnly(List<Column> columnsInConditionExpression, ColumnSchema[] schema) {
        for (Column columnInCondition : columnsInConditionExpression) {
            if (indexOfColumnInConditionOfThisTable(columnInCondition, schema) == -1) {
                return false;
            }
        }
        return true;
    }

    private int indexOfColumnInConditionOfThisTable(Column columnInCondition, ColumnSchema[] schema) {
        for (int i = 0; i < schema.length; i++) {
            if (schema[i].matchColumn(columnInCondition)) {
                return i;
            }
        }
        return -1;
    }

    public Map<Expression, List<Column>> getNonExclusiveConditions() {
        return conditionColumnMap;
    }
}
