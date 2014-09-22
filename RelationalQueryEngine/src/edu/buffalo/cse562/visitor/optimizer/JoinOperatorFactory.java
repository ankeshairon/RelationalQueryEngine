package edu.buffalo.cse562.visitor.optimizer;

import edu.buffalo.cse562.model.Pair;
import edu.buffalo.cse562.operator.abstractoperators.FilterOperator;
import edu.buffalo.cse562.operator.abstractoperators.JoinOperator;
import edu.buffalo.cse562.operator.abstractoperators.Operator;
import edu.buffalo.cse562.operator.joins.InMemoryHashJoinOperator;
import edu.buffalo.cse562.operator.utils.NestedLoopJoinOperator;
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
//            final FilterOperator filterOperator1 = addConditionsAboutToRenderedUnused(operator1);
//            final FilterOperator filterOperator2 = addConditionsAboutToRenderedUnused(operator2);
//            return new IndexNestedLoopJoinOperator(filterOperator1, filterOperator2, positionsOfJoinColumns.getFirst(), positionsOfJoinColumns.getSecond());
        } else {
            return new InMemoryHashJoinOperator(operator1, operator2, positionsOfJoinColumns.getFirst(), positionsOfJoinColumns.getSecond());
        }
    }

    private FilterOperator addConditionsAboutToRenderedUnused(Operator operator) {
        List<Expression> conditions = null;
        FilterOperator filterOperator = null;

        if (operator instanceof FilterOperator) {
            filterOperator = (FilterOperator) operator;
            conditions = filterOperator.getConditions();
        } else {
            throw new RuntimeException("Filter operator expected");
        }
        if (conditions != null) {
            conditionsRenderedUnused.addAll(conditions);
        }
        return filterOperator;
    }

    public List<Expression> getConditionsRenderedUnused() {
        return conditionsRenderedUnused;
    }
}
