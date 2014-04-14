package edu.buffalo.cse562.visitor;

import edu.buffalo.cse562.data.DOUBLE;
import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.data.Datum.CastException;
import edu.buffalo.cse562.data.LONG;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.schema.Column;

import static edu.buffalo.cse562.data.DatumUtilities.getInstance;

public class EvaluatorAggregate extends EvaluatorExecution {


    public EvaluatorAggregate(ColumnSchema[] schema, Expression expression) {
        this.oldSchema = schema;
        expression.accept(this);
        persistentSymbols = symbols;
        persistentLiterals = literals;
        persistentColumnLiteralIndexes = columnLiteralsIndexes;
    }

    public Datum executeStack(Datum[] tuple) {
        loadSavedStacks();
        this.tuple = tuple;

        while (!symbols.empty()) {
            literals.push(executeExpression());
        }
        return safePopLiteral();
    }

    private Datum executeExpression() {
        //order not to be changed
        Datum dataLeft = safePopLiteral();
        String condition = symbols.pop();
        Datum dataRight = safePopLiteral();


        Double floatLeft = null;
        Double floatRight = null;
        try {
            floatLeft = dataLeft.toDOUBLE();
            floatRight = dataRight.toDOUBLE();
        } catch (CastException e) {
            e.printStackTrace();
        }
        Double result;
        switch (condition) {
            case MULTIPLY:
                result = floatLeft * floatRight;
                break;
            case DIVIDE:
                result = floatLeft / floatRight;
                break;
            case SUBTRACT:
                result = floatLeft - floatRight;
                break;
            case ADD:
                result = floatLeft + floatRight;
                break;
            default:
                throw new UnsupportedOperationException("Malformed stack exception");
        }
        return getInstance(result, dataRight.getType());
    }
    /*
     * Binary Operators
	 */

    @Override
    public void visit(Addition arg0) {
        visitBinaryExpression(arg0, ADD);
    }

    @Override
    public void visit(Division arg0) {
        visitBinaryExpression(arg0, DIVIDE);
    }

    @Override
    public void visit(Multiplication arg0) {
        visitBinaryExpression(arg0, MULTIPLY);
    }

    @Override
    public void visit(Subtraction arg0) {
        visitBinaryExpression(arg0, SUBTRACT);
    }

    @Override
    public void visit(Parenthesis arg0) {
        arg0.getExpression().accept(this);
        symbols.push(PARENTHESIS);
    }

	/*
     * Leaf Nodes
	 */

    @Override
    public void visit(DoubleValue arg0) {
        literals.push(new DOUBLE(arg0.toString()));
    }

    @Override
    public void visit(LongValue arg0) {
        literals.push(new LONG(arg0.getValue()));
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

    private void visitBinaryExpression(BinaryExpression arg0, String operation) {
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();
        rightExpression.accept(this);
        symbols.push(operation);
        leftExpression.accept(this);
    }

    private Datum safePopLiteral() {
        if (!symbols.empty() && PARENTHESIS.equals(symbols.peek())) {
            symbols.pop();
            literals.push(executeExpression());
        }
        Datum literalsPop = literals.pop();
        return literalsPop == null ? popValueFromColumnStack() : literalsPop;
    }
}

