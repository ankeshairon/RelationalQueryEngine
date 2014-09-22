package edu.buffalo.cse562.visitor.optimizer.model;

import edu.buffalo.cse562.model.Pair;
import edu.buffalo.cse562.operator.abstractoperators.Operator;
import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.visitor.optimizer.CrossToJoinOptimizer;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

import java.util.*;

import static edu.buffalo.cse562.schema.SchemaUtils.getColIndexInSchema;

public class JoinPlan implements Comparable<JoinPlan> {
    private CrossToJoinOptimizer optimizer;
    private Set<Expression> conditionsUsed;

    private Operator operator1;
    private Operator operator2;
    private Long dataSize;
    private Pair<Integer, Integer> positionsOfJoinColumns;

    private Long priorityWeight;

    public JoinPlan(Operator operator1, Operator operator2) {
        final Long op1Size = operator1.getProbableTableSize();
        final Long op2Size = operator2.getProbableTableSize();

        if (op1Size > op2Size) {
            this.operator1 = operator1;
            this.operator2 = operator2;
        } else {
            this.operator2 = operator1;
            this.operator1 = operator2;
        }
        dataSize = op1Size * op2Size;
    }

    public void evaluate(CrossToJoinOptimizer optimizer) {
        this.optimizer = optimizer;
        conditionsUsed = new HashSet<>();
        positionsOfJoinColumns = calculatePositionsOfJoinColumns();
        priorityWeight = calculatePriorityWeight();
    }

    private long calculatePriorityWeight() {
        return (positionsOfJoinColumns == null) ? dataSize - Long.MAX_VALUE : dataSize;
    }

    @Override
    public boolean equals(Object obj) {
        return dataSize.equals(((JoinPlan) obj).dataSize);
    }

    @Override
    public int compareTo(JoinPlan that) {
        return that.priorityWeight.compareTo(priorityWeight);
    }

    public void markChosen() {
        final Map<Expression, List<Column>> nonExclusiveConditions = optimizer.getNonExclusiveConditions();
        final Set<Column> columnsUsedUp = optimizer.getColumnsUsedUp();

        for (Expression condition : conditionsUsed) {
            columnsUsedUp.addAll(nonExclusiveConditions.get(condition));
            nonExclusiveConditions.remove(condition);
        }
    }

    public Operator getOperator1() {
        return operator1;
    }

    public Operator getOperator2() {
        return operator2;
    }

    private Pair<Integer, Integer> calculatePositionsOfJoinColumns() {
        final Map<Expression, List<Column>> nonExclusiveConditions = optimizer.getNonExclusiveConditions();

        Iterator<Expression> iterator = nonExclusiveConditions.keySet().iterator();
        Expression condition;
        while (iterator.hasNext()) {
            condition = iterator.next();
            List<Column> columnsInConditionExpression = nonExclusiveConditions.get(condition);
            final Pair<Integer, Integer> indexesOfBothTableColumnsForCondition = getIndexesOfBothTableColumnsForCondition(columnsInConditionExpression, operator1.getSchema(), operator2.getSchema());
            if (indexesOfBothTableColumnsForCondition != null) {
                conditionsUsed.add(condition);
                return indexesOfBothTableColumnsForCondition;
            }
        }
        return null;
    }

    private Pair<Integer, Integer> getIndexesOfBothTableColumnsForCondition(List<Column> columnsInConditionExpression, ColumnSchema[] schema1, ColumnSchema[] schema2) {
        Integer index1 = -1;
        Integer index2 = -1;

        Integer index;
        for (Column columnInCondition : columnsInConditionExpression) {
            if ((index = getColIndexInSchema(columnInCondition, schema1)) != -1) {
                index1 = index;
            } else if ((index = getColIndexInSchema(columnInCondition, schema2)) != -1) {
                index2 = index;
            } else {
                return null;
            }
        }
        return (index1 != -1 && index2 != -1) ? new Pair<>(index1, index2) : null;
    }

    public Pair<Integer, Integer> getPositionsOfJoinColumns() {
        return positionsOfJoinColumns;
    }
}
