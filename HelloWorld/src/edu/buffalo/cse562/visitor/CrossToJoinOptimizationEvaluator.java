package edu.buffalo.cse562.visitor;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrossToJoinOptimizationEvaluator extends AbstractExpressionVisitor {
    private Map<Expression, List<Column>> conditionColumnMap;
    private List<Column> currentColumnListToSave;

    /*
    * This class is to extract a map of (expressions & columns involved in that expression) in the where clause
    * */
    public CrossToJoinOptimizationEvaluator(Expression whereExpression) {
        conditionColumnMap = new HashMap<>();
        currentColumnListToSave = new ArrayList<>();
        whereExpression.accept(this);
    }

    public Map<Expression, List<Column>> getConditionColumnMap() {
        return conditionColumnMap;
    }

    @Override
    public void visit(AndExpression binaryExpression) {
        Expression rightExpression = binaryExpression.getRightExpression();
        rightExpression.accept(this);
        if (!currentColumnListToSave.isEmpty()) {
            conditionColumnMap.put(rightExpression, currentColumnListToSave);
            currentColumnListToSave = new ArrayList<>();
        }

        binaryExpression.getLeftExpression().accept(this);
        if (!currentColumnListToSave.isEmpty()) {
            conditionColumnMap.put(binaryExpression.getLeftExpression(), currentColumnListToSave);
            currentColumnListToSave = new ArrayList<>();
        }
    }

    @Override
    public void visit(OrExpression arg0) {
        //todo add smarter logic. Setting this null at the moment to avoid errors going undetected
        conditionColumnMap = null;
    }

    @Override
    public void visit(Column column) {
        currentColumnListToSave.add(column);
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
