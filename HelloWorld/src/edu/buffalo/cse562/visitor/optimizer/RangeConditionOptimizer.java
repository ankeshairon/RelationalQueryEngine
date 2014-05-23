package edu.buffalo.cse562.visitor.optimizer;

import edu.buffalo.cse562.model.Pair;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.schema.Column;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RangeConditionOptimizer {
    private final Map<Expression, List<Column>> conditionColumnMap;

    public RangeConditionOptimizer(Map<Expression, List<Column>> conditionColumnMap) {
        this.conditionColumnMap = conditionColumnMap;
    }

    public void mergeRangeConditions() {
        final ArrayList<Expression> expressions = new ArrayList<>(conditionColumnMap.keySet());
        if (expressions.size() < 2) {
            return;
        }
        Pair<Integer, Integer> conditionPair = getRangeConditionsPositions(expressions);
        if (conditionPair == null) {
            return;
        }
        Expression mergedRangeCondition = mergeConditions(expressions.get(conditionPair.getFirst()), expressions.get(conditionPair.getSecond()));

        final List<Column> columnList = conditionColumnMap.get(expressions.get(conditionPair.getFirst()));
        conditionColumnMap.remove(expressions.get(conditionPair.getFirst()));
        conditionColumnMap.remove(expressions.get(conditionPair.getSecond()));
        conditionColumnMap.put(mergedRangeCondition, columnList);
    }

    private Expression mergeConditions(Expression firstCondition, Expression secondCondition) {
        String c = firstCondition.toString() + " AND " + secondCondition.toString();
        InputStream inputStream = new ByteArrayInputStream(c.getBytes());
        CCJSqlParser parser = new CCJSqlParser(inputStream);
        try {
            return parser.AndExpression();
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private Pair<Integer, Integer> getRangeConditionsPositions(ArrayList<Expression> expressions) {
        for (int i = 0; i < expressions.size(); i++) {
            if (conditionColumnMap.get(expressions.get(i)).size() != 1 || expressions.get(i).toString().contains("<>")) {
                continue;
            }
            for (int j = i + 1; j < expressions.size(); j++) {
                if (conditionColumnMap.get(expressions.get(j)).size() != 1 || expressions.get(i).toString().contains("<>")) {
                    continue;
                }

                if (conditionColumnMap.get(expressions.get(i)).get(0).toString().equalsIgnoreCase(conditionColumnMap.get(expressions.get(j)).get(0).toString())) {
                    return new Pair<>(i, j);
                }
            }
        }
        return null;
    }
}
