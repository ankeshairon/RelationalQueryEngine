package edu.buffalo.cse562.visitor.optimizer;

import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.visitor.CrossToJoinOptimizationEvaluator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CrossToJoinOptimizer {

    private Map<Expression, List<Column>> conditionColumnMap;

    public CrossToJoinOptimizer(Expression where) {
        conditionColumnMap = new CrossToJoinOptimizationEvaluator(where).getConditionColumnMap();
    }

    public List<Expression> getListOfConditionsExclusiveToThisTable(ColumnSchema[] schema) {
        List<Expression> conditionalExpressions = new ArrayList<>();

        for (Expression condition : conditionColumnMap.keySet()) {
            List<Column> columnsInConditionExpression = conditionColumnMap.get(condition);
            if (allColumnsForThisConditionAreOfThisTableOnly(columnsInConditionExpression, schema)) {
                conditionalExpressions.add(condition);
                conditionColumnMap.remove(condition);
            }
        }
        return conditionalExpressions;
    }

    private boolean allColumnsForThisConditionAreOfThisTableOnly(List<Column> columnsInConditionExpression, ColumnSchema[] schema) {
        for (Column columnInCondition : columnsInConditionExpression) {
            if (!isColumnInConditionOfThisTableOnly(columnInCondition, schema)) {
                return false;
            }
        }
        return true;
    }

    private boolean isColumnInConditionOfThisTableOnly(Column columnInCondition, ColumnSchema[] schema) {
        for (ColumnSchema columnSchema : schema) {
            if (!columnSchema.matchColumn(columnInCondition)) {
                return false;
            }
        }
        return true;
    }

    public Set<Expression> getNonExclusiveConditionClauses(){
        return conditionColumnMap.keySet();
    }
}
