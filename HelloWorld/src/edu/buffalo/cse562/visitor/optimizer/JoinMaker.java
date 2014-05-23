package edu.buffalo.cse562.visitor.optimizer;

import edu.buffalo.cse562.operator.JoinOperator;
import edu.buffalo.cse562.operator.Operator;
import edu.buffalo.cse562.operator.SelectionOperator;
import edu.buffalo.cse562.visitor.optimizer.model.JoinPlan;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

import java.io.File;
import java.util.*;

public class JoinMaker {
    private final List<Operator> inputOperators;
    private final File swapDir;
    private CrossToJoinOptimizer optimizer;


    public JoinMaker(Expression where, List<Operator> inputOperators, File swapDir) {
        this.inputOperators = inputOperators;
        this.swapDir = swapDir;
        optimizer = new CrossToJoinOptimizer(where);
    }


    public Operator getOptimizedChainedJoinOperator() {
        List<Operator> selectionScanOperators = convertScanToSelectionOperators();
        return createOptimizedJoinPlan(selectionScanOperators);
    }

    private List<Operator> convertScanToSelectionOperators() {
        List<Operator> selectionScanOperators = new ArrayList<>();
        Operator hybridOperator;

        for (Operator inputOperator : inputOperators) {
            Map<Expression, List<Column>> exclusiveConditionsColumnMap = optimizer.getConditionsExclusiveToTable(inputOperator.getSchema());
            if (exclusiveConditionsColumnMap.isEmpty()) {
                hybridOperator = inputOperator;
            } else {
                hybridOperator = new IndexedOperatorOptimizer().getHybridOperator(inputOperator, exclusiveConditionsColumnMap);
            }
//            size = exclusiveConditionsColumnMap.size();
            selectionScanOperators.add(hybridOperator);
        }
        return selectionScanOperators;
    }

    private Operator createOptimizedJoinPlan(List<Operator> inputOperators) {
        JoinOperatorFactory joinOperatorFactory = new JoinOperatorFactory();
        JoinOperator nestedJoinOperator = null;
        Operator operator1;
        Operator operator2;
        int counter = 0;

        final List<JoinPlan> joinPlans = createJoinPlans(inputOperators);
        Iterator<JoinPlan> joinPlansIterator = joinPlans.iterator();
        JoinPlan bestJoinPlan;

        while (inputOperators.size() != 0) {
            bestJoinPlan = joinPlansIterator.next();
            operator1 = bestJoinPlan.getOperator1();
            operator2 = bestJoinPlan.getOperator2();

            if (!inputOperators.contains(operator1) && !inputOperators.contains(operator2)) {
                continue;
            } else {
                if (!inputOperators.contains(operator1)) {

                    bestJoinPlan = new JoinPlan(nestedJoinOperator, operator2);
                    bestJoinPlan.evaluate(optimizer);
                    bestJoinPlan.markChosen();
                    nestedJoinOperator = joinOperatorFactory.getJoinOperator(bestJoinPlan, counter);
                    inputOperators.remove(operator2);

                } else if (!inputOperators.contains(operator2)) {

                    bestJoinPlan = new JoinPlan(nestedJoinOperator, operator1);
                    bestJoinPlan.evaluate(optimizer);
                    bestJoinPlan.markChosen();
                    nestedJoinOperator = joinOperatorFactory.getJoinOperator(bestJoinPlan, counter);
                    inputOperators.remove(operator1);

                } else {

                    bestJoinPlan.markChosen();
                    nestedJoinOperator = joinOperatorFactory.getJoinOperator(bestJoinPlan, counter);
                    inputOperators.remove(operator1);
                    inputOperators.remove(operator2);

                }
            }
//            inputOperators.add(nestedJoinOperator);
            ++counter;
        }
        List<Expression> conditionsRenderedUnused;
        if ((conditionsRenderedUnused = joinOperatorFactory.getConditionsRenderedUnused()) != null && conditionsRenderedUnused.size() != 0) {
            return new SelectionOperator(nestedJoinOperator, conditionsRenderedUnused);
        }
        return nestedJoinOperator;
    }

    private List<JoinPlan> createJoinPlans(List<Operator> inputOperators) {
        List<JoinPlan> joinPlans = new ArrayList<>();
        JoinPlan joinPlan;
        for (int i = 0; i < inputOperators.size(); i++) {
            for (int j = i + 1; j < inputOperators.size(); j++) {
                joinPlan = new JoinPlan(inputOperators.get(i), inputOperators.get(j));
                joinPlans.add(joinPlan);
                joinPlan.evaluate(optimizer);
            }
        }
        Collections.sort(joinPlans);
        return joinPlans;
    }

    public List<Expression> getNonExclusiveConditionClauses() {
        List<Expression> nonExclusiveConditionClauses = new ArrayList<>();
        nonExclusiveConditionClauses.addAll(optimizer.getNonExclusiveConditions().keySet());
        return nonExclusiveConditionClauses;
    }
}
