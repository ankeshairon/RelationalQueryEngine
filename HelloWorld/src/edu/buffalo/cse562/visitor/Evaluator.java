package edu.buffalo.cse562.visitor;

import edu.buffalo.cse562.data.DATE;
import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.data.FLOAT;
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
import java.text.ParseException;
import java.util.Stack;

public class Evaluator extends AbstractExpressionVisitor {
    static Stack<Datum> literals;
    static Stack<String> symbols;
    boolean check = true;

    ColumnSchema[] schema;
    Datum[] tuple;

    public Evaluator(ColumnSchema[] schema, Datum[] tuple) {
        this.schema = schema;
        this.tuple = tuple;
    }

    public boolean getBool() {
        return check;
    }

    public void executeStack() {

        Datum data;
        String condition = symbols.pop();
        switch (condition) {
            //case "*":	data = literals.pop() * (FLOAT)literals.pop().;
            //case "/": data = literals.pop() / literals.pop();
        }
    }
    /*
	 * Logical and Arithmetic operators
	 */

    @Override
    public void visit(Addition arg0) {
        symbols.push("+");
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();

        leftExpression.accept(new Evaluator(schema, tuple));
        rightExpression.accept(new Evaluator(schema, tuple));


    }

    @Override
    public void visit(Division arg0) {
        symbols.push("/");
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();

        leftExpression.accept(new Evaluator(schema, tuple));
        rightExpression.accept(new Evaluator(schema, tuple));

    }

    @Override
    public void visit(Multiplication arg0) {
        symbols.push("*");
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();

        leftExpression.accept(new Evaluator(schema, tuple));
        rightExpression.accept(new Evaluator(schema, tuple));

    }

    @Override
    public void visit(Subtraction arg0) {
        symbols.push("-");
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();

        leftExpression.accept(new Evaluator(schema, tuple));
        rightExpression.accept(new Evaluator(schema, tuple));

    }

    @Override
    public void visit(AndExpression arg0) {
        symbols.push("AND");
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();

        leftExpression.accept(new Evaluator(schema, tuple));
        rightExpression.accept(new Evaluator(schema, tuple));

    }

    @Override
    public void visit(EqualsTo arg0) {
        symbols.push("=");
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();

        leftExpression.accept(new Evaluator(schema, tuple));
        rightExpression.accept(new Evaluator(schema, tuple));

    }

    @Override
    public void visit(GreaterThan arg0) {
        symbols.push(">");
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();

        leftExpression.accept(new Evaluator(schema, tuple));
        rightExpression.accept(new Evaluator(schema, tuple));

    }

    @Override
    public void visit(GreaterThanEquals arg0) {
        symbols.push(">=");
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();

        leftExpression.accept(new Evaluator(schema, tuple));
        rightExpression.accept(new Evaluator(schema, tuple));
    }

    @Override
    public void visit(MinorThan arg0) {
        symbols.push("<");
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();

        leftExpression.accept(new Evaluator(schema, tuple));
        rightExpression.accept(new Evaluator(schema, tuple));
    }

    @Override
    public void visit(MinorThanEquals arg0) {
        symbols.push("<=");
        Expression leftExpression = arg0.getLeftExpression();
        Expression rightExpression = arg0.getRightExpression();

        leftExpression.accept(new Evaluator(schema, tuple));
        rightExpression.accept(new Evaluator(schema, tuple));
    }
	
	/*
	 * Literals or Leaf Nodes of the recursion tree
	 */

    @Override
    public void visit(DoubleValue arg0) {
        FLOAT doubleVal = new FLOAT(arg0.toString());
        literals.push(doubleVal);
    }

    @Override
    public void visit(LongValue arg0) {
        FLOAT longVal = new FLOAT(arg0.toString());
        literals.push(longVal);
    }

    @Override
    public void visit(DateValue arg0) {
        Date date = arg0.getValue();
        try {
            DATE dateVal = new DATE(date.toString());
            literals.push(dateVal);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(StringValue arg0) {
        STRING stringVal = new STRING(arg0.toString());
        literals.push(stringVal);
    }

    @Override
    public void visit(Column arg0) {
        String columnVal = arg0.getColumnName();
        Datum columnTupleVal;
        int count = 0;
        for (ColumnSchema col : schema) {
            if (col.colName.equals(columnVal)) {
                if (col.type == Datum.type.STRING)
                    columnTupleVal = (STRING) tuple[count];
                else if (col.type == Datum.type.LONG)
                    columnTupleVal = (FLOAT) tuple[count];
                else if (col.type == Datum.type.FLOAT)
                    columnTupleVal = (FLOAT) tuple[count];
                else
                    columnTupleVal = (DATE) tuple[count];
                literals.push(columnTupleVal);
                break;
            }
            count++;
        }
    }

}