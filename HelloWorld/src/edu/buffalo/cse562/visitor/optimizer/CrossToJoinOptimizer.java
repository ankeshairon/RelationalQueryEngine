package edu.buffalo.cse562.visitor.optimizer;

import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.visitor.CrossToJoinOptimizationEvaluator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

import java.util.*;

public class CrossToJoinOptimizer {

    private Map<Expression, List<Column>> conditionColumnMap;

    public CrossToJoinOptimizer(Expression where) {
        conditionColumnMap = new CrossToJoinOptimizationEvaluator(where).getConditionColumnMap();
    }

    public List<Expression> getListOfConditionsExclusiveToThisTable(ColumnSchema[] schema) {
        List<Expression> conditionalExpressions = new ArrayList<>();

        Iterator<Expression> iterator = conditionColumnMap.keySet().iterator();
        Expression condition;
        while (iterator.hasNext()) {
            condition = iterator.next();
            List<Column> columnsInConditionExpression = conditionColumnMap.get(condition);
            if (allColumnsForThisConditionAreOfThisTableOnly(columnsInConditionExpression, schema)) {
                conditionalExpressions.add(condition);
                iterator.remove();
            }
        }
        return conditionalExpressions;
    }

    private boolean allColumnsForThisConditionAreOfThisTableOnly(List<Column> columnsInConditionExpression, ColumnSchema[] schema) {
        for (Column columnInCondition : columnsInConditionExpression) {
            if (!isColumnInConditionOfThisTable(columnInCondition, schema)) {
                return false;
            }
        }
        return true;
    }

    private boolean isColumnInConditionOfThisTable(Column columnInCondition, ColumnSchema[] schema) {
        for (ColumnSchema columnSchema : schema) {
            if (columnSchema.matchColumn(columnInCondition)) {
                return true;
            }
        }
        return false;
    }

    public Set<Expression> getNonExclusiveConditionClauses(){
        return conditionColumnMap.keySet();
    }
}
