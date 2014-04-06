package edu.buffalo.cse562.visitor;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.data.Datum.CastException;
import edu.buffalo.cse562.data.FLOAT;
import edu.buffalo.cse562.data.LONG;
import edu.buffalo.cse562.data.STRING;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static edu.buffalo.cse562.data.DatumUtilities.getInstance;

public class EvaluatorSelection extends AbstractExpressionVisitor {

    private Stack<Column> columnLiterals;
    private Stack<Datum> literals;
    private Stack<String> symbols;

    private boolean result;

    private ColumnSchema[] schema;
    private Datum[] tuple;

    private final Stack<Datum> persistentLiterals;
    private final Stack<String> persistentSymbols;
    private final Stack<Column> persistentColumnLiterals;
    private List<EvaluatorSelection> orEvaluators;

    public EvaluatorSelection(ColumnSchema[] schema, List<Expression> conditions) {
        this.schema = schema;
        symbols = new Stack<>();
        literals = new Stack<>();
        columnLiterals = new Stack<>();
        orEvaluators = new ArrayList<>();
        result = true;
        for (Expression condition : conditions) {
            condition.accept(this);
        }
        persistentSymbols = symbols;
        persistentLiterals = literals;
        persistentColumnLiterals = columnLiterals;
    }


    public boolean getResult() {
        return result;
    }

    public void executeStack(Datum[] tuple) throws CastException {
        symbols = (Stack<String>) persistentSymbols.clone();
        literals = (Stack<Datum>) persistentLiterals.clone();
        columnLiterals = (Stack<Column>) persistentColumnLiterals.clone();
        this.tuple = tuple;
        result = true;
        executeAndExpressionsStack();
        if (result && !orEvaluators.isEmpty()) {
            result = executeOrExpressionsStack(tuple);
        }
    }

    private Boolean executeOrExpressionsStack(Datum[] tuple) throws CastException {
        for (EvaluatorSelection orEvaluator : orEvaluators) {
            orEvaluator.executeStack(tuple);
            if (orEvaluator.getResult()) {
                return true;
            }
        }
        return false;
    }

    private void executeAndExpressionsStack() throws CastException {
        /*
         * Execute Stack is not OR operator safe
		 */

        while (!symbols.empty()) {
            String condition = symbols.pop();
            Datum dataLeft = popFromLiteralsStack();
            Datum dataRight = popFromLiteralsStack();

            if (dataRight.getType() == Datum.type.FLOAT || dataRight.getType() == Datum.type.LONG) {
                Float floatLeft = dataLeft.toFLOAT();
                Float floatRight = dataRight.toFLOAT();
                switch (condition) {
                    case "=":
                        result = floatLeft.equals(floatRight);
                        break;
                    case "<>":
                        result = floatLeft.equals(floatRight);
                        result = !result;
                        break;
                    case ">":
                        if (floatLeft <= floatRight) result = false;
                        break;
                    case "<":
                        if (floatLeft >= floatRight) result = false;
                        break;
                    case ">=":
                        if (floatLeft < floatRight) result = false;
                        break;
                    case "<=":
                        if (floatLeft > floatRight) result = false;
                        break;
                    case "*":
                        literals.push(getInstance(floatLeft * floatRight, dataRight.getType()));
                        break;
                    case "/":
                        literals.push(getInstance(floatLeft / floatRight, dataRight.getType()));
                        break;
                    case "-":
                        literals.push(getInstance(floatLeft - floatRight, dataRight.getType()));
                        break;
                    case "+":
                        literals.push(getInstance(floatLeft + floatRight, dataRight.getType()));
                        break;
                    default:
                        throw new UnsupportedOperationException("Malformed stack exception");
                }
                if (!result) break;
            } else if (dataRight.getType() == Datum.type.STRING) {
                String stringLeft = dataLeft.toSTRING();
                String stringRight = dataRight.toSTRING();

                switch (condition) {
                    case "=":
                        result = stringLeft.equals(stringRight);
                        break;
                    case ">":
                        if (stringLeft.compareTo(stringRight) <= 0) result = false;
                        break;
                    case "<":
                        if (stringLeft.compareTo(stringRight) >= 0) result = false;
                        break;
                    case ">=":
                        if (stringLeft.compareTo(stringRight) < 0) result = false;
                        break;
                    case "<=":
                        if (stringLeft.compareTo(stringRight) > 0) result = false;
                        break;
                    case "<>":
                        result = !stringLeft.equals(stringRight);
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported operation or malformed stack");
                }
                if (!result) break;
            }
        }
    }

    private void compressStack(String arithmeticOperator) throws CastException {

        Datum dataLeft = popFromLiteralsStack();
        if (dataLeft == null) {
            undoPopFromLiteralsStack();
            symbols.push(arithmeticOperator);
            return;
        }
        Datum dataRight = popFromLiteralsStack();
        if (dataRight == null) {
            undoPopFromLiteralsStack();
            symbols.push(arithmeticOperator);
            return;
        }

        Float floatLeft = dataLeft.toFLOAT();
        Float floatRight = dataRight.toFLOAT();

        switch (arithmeticOperator) {
            case "*":
                literals.push(getInstance(floatLeft * floatRight, dataRight.getType()));
                break;
            case "/":
                literals.push(getInstance(floatLeft / floatRight, dataRight.getType()));
                break;
            case "-":
                literals.push(getInstance(floatLeft - floatRight, dataRight.getType()));
                break;
            case "+":
                literals.push(getInstance(floatLeft + floatRight, dataRight.getType()));
                break;
            default:
                throw new UnsupportedOperationException("Unexpected operation" + arithmeticOperator);
        }
    }

	/*
     * Logical and Arithmetic operators
	 */

    @Override
    public void visit(Addition arg0) {
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();
        rightExpression.accept(this);
        leftExpression.accept(this);

        try {
            this.compressStack("+");
        } catch (CastException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void visit(Division arg0) {
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();

        rightExpression.accept(this);
        leftExpression.accept(this);

        try {
            this.compressStack("/");
        } catch (CastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(Multiplication arg0) {
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();

        rightExpression.accept(this);
        leftExpression.accept(this);

        try {
            this.compressStack("*");
        } catch (CastException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void visit(Subtraction arg0) {
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();

        rightExpression.accept(this);
        leftExpression.accept(this);

        try {
            this.compressStack("-");
        } catch (CastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(AndExpression arg0) {
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();

        rightExpression.accept(this);
        leftExpression.accept(this);
    }

    @Override
    public void visit(OrExpression arg0) {
        orEvaluators.add(new EvaluatorSelection(schema, Arrays.asList(arg0.getLeftExpression())));
        orEvaluators.add(new EvaluatorSelection(schema, Arrays.asList(arg0.getRightExpression())));
    }

    @Override
    public void visit(Parenthesis arg0) {
        arg0.getExpression().accept(this);
    }

    @Override
    public void visit(EqualsTo arg0) {
        symbols.push("=");
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();

        rightExpression.accept(this);
        leftExpression.accept(this);
    }

    @Override
    public void visit(NotEqualsTo arg0) {
        symbols.push("<>");
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();

        rightExpression.accept(this);
        leftExpression.accept(this);
    }


    @Override
    public void visit(GreaterThan arg0) {
        symbols.push(">");
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();

        rightExpression.accept(this);
        leftExpression.accept(this);
    }

    @Override
    public void visit(GreaterThanEquals arg0) {
        symbols.push(">=");
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();

        rightExpression.accept(this);
        leftExpression.accept(this);
    }

    @Override
    public void visit(MinorThan arg0) {
        symbols.push("<");
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();

        rightExpression.accept(this);
        leftExpression.accept(this);

    }

    @Override
    public void visit(MinorThanEquals arg0) {
        symbols.push("<=");
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();

        rightExpression.accept(this);
        leftExpression.accept(this);
    }
    /*
     *literals or Leaf Nodes of the recursion tree
	 */

    @Override
    public void visit(DoubleValue arg0) {
        FLOAT doubleVal = new FLOAT(arg0.toString());
        literals.push(doubleVal);
    }

    @Override
    public void visit(LongValue arg0) {
        literals.push(new LONG(arg0.getValue()));
    }

    @Override
    public void visit(DateValue arg0) {
        Date date = arg0.getValue();
        STRING dateVal = new STRING(date.toString());
        literals.push(dateVal);
    }

    @Override
    public void visit(StringValue arg0) {
        String str = arg0.toString();
        str = str.replaceAll("'", "");
        STRING stringVal = new STRING(str);
        literals.push(stringVal);

    }

    @Override
    public void visit(Column column) {
        literals.push(null);
        columnLiterals.push(column);
//        popValueFromColumnStack(column);
    }

    @Override
    public void visit(Function function) {
        if (function.getName().equalsIgnoreCase("date")) {
            Expression expr = (Expression) function.getParameters().getExpressions().get(0);
            expr.accept(this);
        } else {
            super.visit(function);
        }
    }

    private Datum popFromLiteralsStack() {
        Datum literalsPop = literals.pop();
        return literalsPop == null ? popValueFromColumnStack() : literalsPop;
    }

    private void undoPopFromLiteralsStack() {
        literals.push(null);
    }

    private Datum popValueFromColumnStack() {
        Column column;
        if (tuple != null) {
            column = columnLiterals.pop();
            for (int i = 0; i < schema.length; i++) {
                if (schema[i].matchColumn(column)) {
                    return tuple[i];
                }
            }
            throw new UnsupportedOperationException("Unable to read detect column " + column.toString() + " in schema " + printSchema() + " Malformed schema?");
        }
        throw new UnsupportedOperationException("Attempt to read column value of a null tuple. Not happening!");
    }

    private String printSchema() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (ColumnSchema s : schema) {
            stringBuilder.append(s.toString()).append(",\n");
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }


}
