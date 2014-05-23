package edu.buffalo.cse562.visitor.optimizer;

import edu.buffalo.cse562.model.Pair;
import edu.buffalo.cse562.operator.*;
import edu.buffalo.cse562.operator.indexscan.IndexScanOperator;
import edu.buffalo.cse562.visitor.optimizer.model.JoinPlan;
import net.sf.jsqlparser.expression.Expression;

import java.util.ArrayList;
import java.util.List;

public class JoinOperatorFactory {

    private List<Expression> conditionsRenderedUnused;

    public JoinOperatorFactory() {
        conditionsRenderedUnused = new ArrayList<>();
    }

    public JoinOperator getJoinOperator(JoinPlan joinPlan, int counter) {
        final Pair<Integer, Integer> positionsOfJoinColumns = joinPlan.getPositionsOfJoinColumns();
        final Operator operator1 = joinPlan.getOperator1();
        final Operator operator2 = joinPlan.getOperator2();

        if (positionsOfJoinColumns == null) {
            return new NestedLoopJoinOperator(operator1, operator2);
//        } else if (counter == 0) {
//            addConditionsAboutToRenderedUnused(operator1);
//            addConditionsAboutToRenderedUnused(operator2);
//            return new IndexNestedLoopJoinOperator(operator1, operator2, positionsOfJoinColumns.getFirst(), positionsOfJoinColumns.getSecond());
        } else {
            return new InMemoryHashJoinOperator(operator1, operator2, positionsOfJoinColumns.getFirst(), positionsOfJoinColumns.getSecond());
        }
    }

    private void addConditionsAboutToRenderedUnused(Operator operator) {
        List<Expression> conditions = null;
        if (operator instanceof SelectionOperator) {
            conditions = ((SelectionOperator) operator).getConditions();
        } else if (operator instanceof IndexScanOperator) {
            conditions = ((IndexScanOperator) operator).getConditions();
        }
        if (conditions != null) {
            conditionsRenderedUnused.addAll(conditions);
        }
    }

    public List<Expression> getConditionsRenderedUnused() {
        return conditionsRenderedUnused;
    }
}
