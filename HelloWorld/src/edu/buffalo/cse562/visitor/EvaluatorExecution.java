package edu.buffalo.cse562.visitor;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;

import java.util.Stack;

public abstract class EvaluatorExecution extends AbstractExpressionVisitor {
    protected final static String ADD = "+";
    protected final static String DIVIDE = "/";
    protected final static String MULTIPLY = "*";
    protected final static String SUBTRACT = "-";
    protected final static String PARENTHESIS = "()";

    protected Stack<Datum> persistentLiterals;
    protected Stack<String> persistentSymbols;
    protected Stack<Integer> persistentColumnLiteralIndexes;

    protected Stack<Integer> columnLiteralsIndexes;
    protected Stack<Datum> literals;
    protected Stack<String> symbols;

    protected ColumnSchema[] oldSchema;
    protected Datum[] tuple;

    protected EvaluatorExecution() {
        symbols = new Stack<>();
        literals = new Stack<>();
        columnLiteralsIndexes = new Stack<>();
    }

    protected void loadSavedStacks() {
        columnLiteralsIndexes = (Stack<Integer>) persistentColumnLiteralIndexes.clone();
        symbols = (Stack<String>) persistentSymbols.clone();
        literals = (Stack<Datum>) persistentLiterals.clone();
    }


    protected Datum popValueFromColumnStack() {
        Integer columnIndex = columnLiteralsIndexes.pop();
        return tuple[columnIndex];
    }

    protected String printSchema() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (ColumnSchema s : oldSchema) {
            stringBuilder.append(s.toString()).append(",\n");
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
