package edu.buffalo.cse562.visitor;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.data.Datum.CastException;
import edu.buffalo.cse562.data.FLOAT;
import edu.buffalo.cse562.data.LONG;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.schema.Column;

import java.util.Stack;

import static edu.buffalo.cse562.data.DatumUtilities.getInstance;

public class EvaluatorAggregate extends AbstractExpressionVisitor {

    private Datum[] tuple;
    private ColumnSchema[] oldSchema;
    private Stack<Datum> literals;
    private Stack<String> symbols;

    public EvaluatorAggregate(Datum[] tuple, ColumnSchema[] oldSchema, Expression evalExpresssion) {
        this.tuple = tuple;
        this.oldSchema = oldSchema;
        literals = new Stack<>();
        symbols = new Stack<>();
        evaluateExpression(evalExpresssion);
    }

    private void evaluateExpression(Expression expression) {
        final String expressionString = expression.toString();

        Datum columnTupleVal;
        for (int i = 0; i < oldSchema.length; i++) {
            if(oldSchema[i].matchColumnNameOnly(expressionString)){
                columnTupleVal = tuple[i];
                literals.push(columnTupleVal);
                return;
            }
        }
        expression.accept(this);
    }

    public Datum executeStack() throws CastException {
        while (!symbols.empty()) {
            literals.push(executeExpression());
        }
        return literals.pop();

    }

    private Datum executeExpression() throws CastException {
        String condition = symbols.pop();
        Datum dataRight = literals.pop();
        Datum dataLeft = literals.pop();
        Float floatLeft = dataLeft.toFLOAT();
        Float floatRight = dataRight.toFLOAT();

        switch (condition) {
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
        return literals.pop();
    }
    /*
     * Binary Operators
	 */

    @Override
    public void visit(Addition arg0) {
        symbols.push("+");
        visitBinaryExpression(arg0);
    }

    @Override
    public void visit(Division arg0) {
        symbols.push("/");
        visitBinaryExpression(arg0);
    }

    @Override
    public void visit(Multiplication arg0) {
//    	System.out.println("*");
        symbols.push("*");
        visitBinaryExpression(arg0);
    }

    @Override
    public void visit(Subtraction arg0) {
//    	System.out.println("-");
        symbols.push("-");
        visitBinaryExpression(arg0);
    }

    @Override
    public void visit(Parenthesis arg0) {
//    	System.out.println("()");
        Expression expr = arg0.getExpression();
        expr.accept(this);
        try {
            literals.push(executeExpression());
        } catch (CastException e) {
            e.printStackTrace();
        }
    }

	/*
     * Leaf Nodes
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

    /*
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
    */
    @Override
    public void visit(Column column) {
        Datum columnTupleVal;
        for (int i = 0; i < oldSchema.length; i++) {
            if(oldSchema[i].matchColumn(column)){
                columnTupleVal = tuple[i];
                literals.push(columnTupleVal);
                return;
            }
        }
        throw new UnsupportedOperationException("Unable to read detect column " + column.toString() + " in schema " + printSchema());
    }

    private void visitBinaryExpression(BinaryExpression arg0) {
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();
        leftExpression.accept(this);
        rightExpression.accept(this);
    }

    private String printSchema() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (ColumnSchema s : oldSchema) {
            stringBuilder.append(s.toString()).append(",\n");
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

}

