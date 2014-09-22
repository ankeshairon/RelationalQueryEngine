package edu.buffalo.cse562.visitor.optimizer;

import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.visitor.CrossToJoinOptimizationVisitor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

import java.util.*;

import static edu.buffalo.cse562.schema.SchemaUtils.getColIndexInSchema;

public class CrossToJoinOptimizer {

    private Map<Expression, List<Column>> conditionColumnMap;

    private Set<Column> columnsUsedUp;

    public CrossToJoinOptimizer(Expression where) {
        conditionColumnMap = new CrossToJoinOptimizationVisitor(where).getConditionColumnMap();
        columnsUsedUp = new HashSet<>();
    }

    public Map<Expression, List<Column>> getConditionsExclusiveToTable(ColumnSchema[] schema) {
        Map<Expression, List<Column>> conditionColumnMapExclusiveTo = new HashMap<>();

        Iterator<Expression> iterator = conditionColumnMap.keySet().iterator();
        Expression condition;
        while (iterator.hasNext()) {
            condition = iterator.next();
            List<Column> columnsInConditionExpression = conditionColumnMap.get(condition);
            if (allColumnsForConditionOfThisTableOnly(columnsInConditionExpression, schema)) {
                conditionColumnMapExclusiveTo.put(condition, conditionColumnMap.get(condition));
                columnsUsedUp.addAll(conditionColumnMap.get(condition));
                iterator.remove();
            }
        }
        return conditionColumnMapExclusiveTo;
    }

    /*public Pair<Integer, Integer> getIndexesOfJoinColumns(ColumnSchema[] schema1, ColumnSchema[] schema2) {
        Iterator<Expression> iterator = conditionColumnMap.keySet().iterator();
        Expression condition;
        while (iterator.hasNext()) {
            condition = iterator.next();
            List<Column> columnsInConditionExpression = conditionColumnMap.get(condition);
            final Pair<Integer, Integer> indexesOfBothTableColumnsForCondition = getIndexesOfBothTableColumnsForCondition(columnsInConditionExpression, schema1, schema2);
            if (indexesOfBothTableColumnsForCondition != null) {
                columnsUsedUp.addAll(conditionColumnMap.get(condition));
                iterator.remove();
                return indexesOfBothTableColumnsForCondition;
            }
        }
        return null;
    }*/

    public Map<Expression, List<Column>> getNonExclusiveConditions() {
        return conditionColumnMap;
    }

    public Set<Column> getAllColumnsUsedInWhereClause() {
        Set<Column> allColumns = new HashSet<>();
        allColumns.addAll(columnsUsedUp);

        for (List<Column> columns : conditionColumnMap.values()) {
            allColumns.addAll(columns);
        }
        return allColumns;
    }


    /*private Pair<Integer, Integer> getIndexesOfBothTableColumnsForCondition(List<Column> columnsInConditionExpression, ColumnSchema[] schema1, ColumnSchema[] schema2) {
        Integer index1 = -1;
        Integer index2 = -1;

        Integer index;
        for (Column columnInCondition : columnsInConditionExpression) {
            if ((index = getIndexOfColumnInSchema(columnInCondition, schema1)) != -1) {
                index1 = index;
            } else if ((index = getIndexOfColumnInSchema(columnInCondition, schema2)) != -1) {
                index2 = index;
            } else {
                return null;
            }
        }
        return (index1 != -1 && index2 != -1) ? new Pair<>(index1, index2) : null;
    }*/

    private boolean allColumnsForConditionOfThisTableOnly(List<Column> columnsInConditionExpression, ColumnSchema[] schema) {
        for (Column columnInCondition : columnsInConditionExpression) {
            if (getColIndexInSchema(columnInCondition, schema) == -1) {
                return false;
            }
        }
        return true;
    }

    public Set<Column> getColumnsUsedUp() {
        return columnsUsedUp;
    }
}
