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
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;

import java.sql.Date;
import java.util.List;
import java.util.Stack;

import static edu.buffalo.cse562.data.DatumUtilities.getInstance;

public class EvaluatorSelection extends AbstractExpressionVisitor {

    Stack<Column> columnLiterals;
    Stack<Datum> literals;
    Stack<String> symbols;
    private boolean bool;
    ColumnSchema[] schema;
    Datum[] tuple;
    final Stack<Datum> persistentLiterals;
    final Stack<String> persistentSymbols;
    final Stack<Column> persistentColumnLiterals;

/*    public EvaluatorSelection(ColumnSchema[] schema, Datum[] tuple) {
        this.schema = schema;
        this.tuple = tuple;
        this.literals = new Stack<>();
        this.symbols = new Stack<>();
        bool = true;
    }

    public EvaluatorSelection(ColumnSchema[] schema, Datum[] tuple, Stack<Datum> literals, Stack<String> symbols) {
        this.schema = schema;
        this.tuple = tuple;
        this.literals = literals;
        this.symbols = symbols;
    }*/

    public EvaluatorSelection(ColumnSchema[] schema, List<Expression> conditions) {
        this.schema = schema;
        symbols = new Stack<>();
        literals = new Stack<>();
        columnLiterals = new Stack<>();
        bool = true;
        for (Expression condition : conditions) {
            condition.accept(this);
        }
        persistentSymbols = symbols;
        persistentLiterals = literals;
        persistentColumnLiterals = columnLiterals;
    }


    public boolean getBool() {
        return bool;
    }

    public void executeStack(Datum[] tuple) throws CastException {
        symbols = (Stack<String>) persistentSymbols.clone();
        literals = (Stack<Datum>) persistentLiterals.clone();
        columnLiterals = (Stack<Column>) persistentColumnLiterals.clone();
        this.tuple = tuple;
        bool = true;
        executeStack();
    }

    private void executeStack() throws CastException {
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
                        bool = floatLeft.equals(floatRight);
                        break;
                    case ">":
                        if (floatLeft <= floatRight) bool = false;
                        break;
                    case "<":
                        if (floatLeft >= floatRight) bool = false;
                        break;
                    case ">=":
                        if (floatLeft < floatRight) bool = false;
                        break;
                    case "<=":
                        if (floatLeft > floatRight) bool = false;
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
                if (!bool)
                    break;
            } else if (dataRight.getType() == Datum.type.STRING) {
                String stringLeft = dataLeft.toSTRING();
                String stringRight = dataRight.toSTRING();

                switch (condition) {
                    case "=":
                        bool = stringLeft.equals(stringRight);
                        break;
                    case ">":
                        if (stringLeft.compareTo(stringRight) <= 0)
                            bool = false;
                        break;
                    case "<":
                        if (stringLeft.compareTo(stringRight) >= 0)
                            bool = false;
                        break;
                    case ">=":
                        if (stringLeft.compareTo(stringRight) < 0)
                            bool = false;
                        break;
                    case "<=":
                        if (stringLeft.compareTo(stringRight) > 0)
                            bool = false;
                        break;
                    default:
                        throw new UnsupportedOperationException("Malformed stack exception");
                }
                if (!bool)
                    break;
            }
        }
    }

    private void compressStack(String arithmeticOperator) throws CastException {

        Datum dataLeft = popFromLiteralsStack();
        if(dataLeft == null){
            undoPopFromLiteralsStack();
            symbols.push(arithmeticOperator);
            return;
        }
        Datum dataRight = popFromLiteralsStack();
        if(dataRight == null){
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
    public void visit(EqualsTo arg0) {
        symbols.push("=");
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
        if (tuple != null) {
            Column column = columnLiterals.pop();
            for (int i = 0; i < schema.length; i++) {
                if (schema[i].matchColumn(column)) {
                    return tuple[i];
                }
            }
        }
        return null;
    }
}
