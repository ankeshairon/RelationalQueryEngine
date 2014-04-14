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

import static edu.buffalo.cse562.data.DatumUtilities.getInstance;

public class EvaluatorSelection extends EvaluatorExecution {

    private List<EvaluatorSelection> orEvaluators;
    private boolean result;

    public EvaluatorSelection(ColumnSchema[] schema, List<Expression> conditions) {
        this.oldSchema = schema;
        orEvaluators = new ArrayList<>();
        result = true;
        for (Expression condition : conditions) {
            condition.accept(this);
        }
        persistentSymbols = symbols;
        persistentLiterals = literals;
        persistentColumnLiteralIndexes = columnLiteralsIndexes;
    }


    public boolean getResult() {
        return result;
    }

    public void executeStack(Datum[] tuple) throws CastException {
        loadSavedStacks();
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
            Datum dataLeft = safePopLiteral();
            Datum dataRight = safePopLiteral();

            if (dataRight.getType() == Datum.type.FLOAT || dataRight.getType() == Datum.type.LONG) {
                Double floatLeft = dataLeft.toFLOAT();
                Double floatRight = dataRight.toFLOAT();
                switch (condition) {
                    case "=":
                        result = floatLeft.equals(floatRight);
                        break;
                    case "<>":
                        result = !floatLeft.equals(floatRight);
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
                    case MULTIPLY:
                        literals.push(getInstance(floatLeft * floatRight, dataRight.getType()));
                        break;
                    case DIVIDE:
                        literals.push(getInstance(floatLeft / floatRight, dataRight.getType()));
                        break;
                    case SUBTRACT:
                        literals.push(getInstance(floatLeft - floatRight, dataRight.getType()));
                        break;
                    case ADD:
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

        Datum dataLeft = safePopLiteral();
        if (dataLeft == null) {
            undoPopFromLiteralsStack();
            symbols.push(arithmeticOperator);
            return;
        }
        Datum dataRight = safePopLiteral();
        if (dataRight == null) {
            undoPopFromLiteralsStack();
            symbols.push(arithmeticOperator);
            return;
        }

        Double floatLeft = dataLeft.toFLOAT();
        Double floatRight = dataRight.toFLOAT();

        switch (arithmeticOperator) {
            case MULTIPLY:
                literals.push(getInstance(floatLeft * floatRight, dataRight.getType()));
                break;
            case DIVIDE:
                literals.push(getInstance(floatLeft / floatRight, dataRight.getType()));
                break;
            case SUBTRACT:
                literals.push(getInstance(floatLeft - floatRight, dataRight.getType()));
                break;
            case ADD:
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
        visitBinaryExpression(arg0);

        try {
            this.compressStack(ADD);
        } catch (CastException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void visit(Division arg0) {
        visitBinaryExpression(arg0);

        try {
            this.compressStack(DIVIDE);
        } catch (CastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(Multiplication arg0) {
        visitBinaryExpression(arg0);

        try {
            this.compressStack(MULTIPLY);
        } catch (CastException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void visit(Subtraction arg0) {
        visitBinaryExpression(arg0);

        try {
            this.compressStack(SUBTRACT);
        } catch (CastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(AndExpression arg0) {
        visitBinaryExpression(arg0);
    }

    @Override
    public void visit(OrExpression arg0) {
        orEvaluators.add(new EvaluatorSelection(oldSchema, Arrays.asList(arg0.getLeftExpression())));
        orEvaluators.add(new EvaluatorSelection(oldSchema, Arrays.asList(arg0.getRightExpression())));
    }

    @Override
    public void visit(Parenthesis arg0) {
        arg0.getExpression().accept(this);
    }

    @Override
    public void visit(EqualsTo arg0) {
        symbols.push("=");
        visitBinaryExpression(arg0);
    }

    @Override
    public void visit(NotEqualsTo arg0) {
        symbols.push("<>");
        visitBinaryExpression(arg0);
    }


    @Override
    public void visit(GreaterThan arg0) {
        symbols.push(">");
        visitBinaryExpression(arg0);
    }

    @Override
    public void visit(GreaterThanEquals arg0) {
        symbols.push(">=");
        visitBinaryExpression(arg0);
    }

    @Override
    public void visit(MinorThan arg0) {
        symbols.push("<");
        visitBinaryExpression(arg0);
    }

    @Override
    public void visit(MinorThanEquals arg0) {
        symbols.push("<=");
        visitBinaryExpression(arg0);
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

        for (int i = 0; i < oldSchema.length; i++) {
            if (oldSchema[i].matchColumn(column)) {
                columnLiteralsIndexes.push(i);
                return;
            }
        }
        throw new UnsupportedOperationException("Unable to read column " + column.toString() + " in oldSchema " + printSchema());
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

    private void visitBinaryExpression(BinaryExpression arg0) {
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();
        rightExpression.accept(this);
        leftExpression.accept(this);
    }

    private Datum safePopLiteral() {
        Datum literalsPop = literals.pop();
        return literalsPop == null ? popValueFromColumnStack() : literalsPop;
    }

    private void undoPopFromLiteralsStack() {
        literals.push(null);
    }
}
