package edu.buffalo.cse562.visitor;

import edu.buffalo.cse562.model.ColumnWrapper;
import edu.buffalo.cse562.visitor.optimizer.RangeConditionOptimizer;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;

import java.util.*;

public class CrossToJoinOptimizationVisitor extends AbstractExpressionVisitor {
    private Map<Expression, List<Column>> conditionColumnMap;
    private Set<Column> currentColumnSetToSave;
    private int pendingOrs;

    /*
    * This class is to extract a map of (expressions & columns involved in that expression) in the where clause
    * */
    public CrossToJoinOptimizationVisitor(Expression whereExpression) {
        conditionColumnMap = new HashMap<>();
        currentColumnSetToSave = new HashSet<>();
        pendingOrs = 0;
        whereExpression.accept(this);
        if (!currentColumnSetToSave.isEmpty()) {
            conditionColumnMap.put(whereExpression, new ArrayList<>(currentColumnSetToSave));
        }
    }

    public Map<Expression, List<Column>> getConditionColumnMap() {
        if (conditionColumnMap != null && conditionColumnMap.size() != 0) {
            new RangeConditionOptimizer(conditionColumnMap).mergeRangeConditions();
        }
        return conditionColumnMap;
    }

    @Override
    public void visit(AndExpression binaryExpression) {
        visitBinaryExpression(binaryExpression);
    }

    @Override
    public void visit(OrExpression binaryExpression) {
        ++pendingOrs;
        visitBinaryExpression(binaryExpression);
        if (--pendingOrs == 0) {
            saveExpressionsCollectedForCondition(binaryExpression);
        }
    }

    private void visitBinaryExpression(BinaryExpression binaryExpression) {
        Expression rightExpression = binaryExpression.getRightExpression();
        rightExpression.accept(this);
        if (!currentColumnSetToSave.isEmpty() && (pendingOrs == 0)) {
            saveExpressionsCollectedForCondition(rightExpression);
        }

        binaryExpression.getLeftExpression().accept(this);
        if (!currentColumnSetToSave.isEmpty() && (pendingOrs == 0)) {
            saveExpressionsCollectedForCondition(binaryExpression.getLeftExpression());
        }
    }

    private void saveExpressionsCollectedForCondition(Expression binaryExpression) {
        conditionColumnMap.put(binaryExpression, new ArrayList<>(currentColumnSetToSave));
        currentColumnSetToSave = new HashSet<>();
    }

    @Override
    public void visit(Parenthesis parenthesizedExpression) {
        parenthesizedExpression.getExpression().accept(this);
    }

    @Override
    public void visit(Column column) {
        currentColumnSetToSave.add(new ColumnWrapper(column));
    }

    @Override
    public void visit(Addition arg0) {
        checkForColumns(arg0);
    }

    @Override
    public void visit(Division arg0) {
        checkForColumns(arg0);
    }

    @Override
    public void visit(Multiplication arg0) {
        checkForColumns(arg0);
    }

    @Override
    public void visit(Subtraction arg0) {
        checkForColumns(arg0);
    }

    @Override
    public void visit(EqualsTo arg0) {
        checkForColumns(arg0);
    }

    @Override
    public void visit(NotEqualsTo arg0) {
        checkForColumns(arg0);
    }

    @Override
    public void visit(GreaterThan arg0) {
        checkForColumns(arg0);
    }

    @Override
    public void visit(GreaterThanEquals arg0) {
        checkForColumns(arg0);
    }

    @Override
    public void visit(MinorThan arg0) {
        checkForColumns(arg0);
    }

    @Override
    public void visit(MinorThanEquals arg0) {
        checkForColumns(arg0);
    }

    @Override
    public void visit(DoubleValue arg0) {
        //do nothing
    }

    @Override
    public void visit(LongValue arg0) {
        //do nothing
    }

    @Override
    public void visit(DateValue arg0) {
        //do nothing
    }

    @Override
    public void visit(StringValue arg0) {
        //do nothing
    }

    @Override
    public void visit(Function function) {
        if (!function.getName().equalsIgnoreCase("date")) {
            ((Expression) function.getParameters().getExpressions().get(0)).accept(this);
        }
    }

    private void checkForColumns(BinaryExpression binaryExpression) {
        binaryExpression.getRightExpression().accept(this);
        binaryExpression.getLeftExpression().accept(this);
    }


}
