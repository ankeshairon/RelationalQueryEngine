package edu.buffalo.cse562.visitor.optimizer;

import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CrossToJoinOptimizer {

    private Map<Expression, List<Column>> conditionColumnMap;

    public CrossToJoinOptimizer(Map<Expression, List<Column>> conditionColumnMap) {
        this.conditionColumnMap = conditionColumnMap;
    }

    public List<Expression> canPullASelectInCrossToMakeAJoin(ColumnSchema[] schema) {
        List<Expression> conditionalExpressions = new ArrayList<>();

        for (Expression condition : conditionColumnMap.keySet()) {
            List<Column> columnsInConditionExpression = conditionColumnMap.get(condition);
            if (allColumnsForThisConditionAreOfThisTableOnly(columnsInConditionExpression, schema)) {
                conditionalExpressions.add(condition);
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
}
