package edu.buffalo.cse562.model.aggregation;

import edu.buffalo.cse562.comparator.TupleComparator;
import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.data.FLOAT;
import edu.buffalo.cse562.data.FRACTION;
import edu.buffalo.cse562.data.LONG;
import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.visitor.EvaluatorAggregate;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static edu.buffalo.cse562.Constants.INDEX_INDICATING_EXPRESSION_INSIDE_FUNCTION;
import static edu.buffalo.cse562.Constants.INDEX_INDICATING_FUNCTION;

public class AggregationProcessor {

    private final ColumnSchema[] oldSchema;
    private final ColumnSchema[] newSchema;

    private List<Datum[]> resultDatumWithUniqueValuesOfGroupByElements;

    private Integer[] newSchemaIndexesRelativeToOldSchema;
    private TupleComparator tupleComparator;


    public AggregationProcessor(ColumnSchema[] oldSchema, ColumnSchema[] newSchema, Integer[] newSchemaIndexesRelativeToOldSchema) {
        this.oldSchema = oldSchema;
        this.newSchema = newSchema;
        this.newSchemaIndexesRelativeToOldSchema = newSchemaIndexesRelativeToOldSchema;
        tupleComparator = new TupleComparator(createLinkedHashMapOfIndexes());
        resultDatumWithUniqueValuesOfGroupByElements = new ArrayList<>();
    }


    public void process(Datum[] oldTuple) {
        //check if the tuple received has resultDatumWithUniqueValuesOfGroupByElements
        //if yes, then update the corresponding data values
        //else add new and add corresponding data values

        Datum[] newTuple = convertTupleToNewSchema(oldTuple);

        if (!(newTuple == null)) {
            int index;
            for (index = 0; index < resultDatumWithUniqueValuesOfGroupByElements.size(); index++) {
                if (matchesTheCombinationOfGroupByElements(newTuple, index)) {
                    Datum[] newAggregatedDatum = resultDatumWithUniqueValuesOfGroupByElements.get(index);
                    for (int i = 0; i < newSchemaIndexesRelativeToOldSchema.length; i++) {
                        if (newTuple[i] == null) {
                            continue;
                        }
                        if (newSchemaIndexesRelativeToOldSchema[i] >= 0) {
                            newAggregatedDatum[i] = newTuple[i];
                        } else {
                            newAggregatedDatum[i] = getUpdatedAggregateSpecificParams(newAggregatedDatum[i], newTuple[i], i);
                        }
                    }
                    resultDatumWithUniqueValuesOfGroupByElements.set(index, newAggregatedDatum);
                    break;
                }
            }
            if (isANewUniqueCombinationOfGroupByElements(index)) {
                Datum[] newAggregatedDatum = new Datum[newSchema.length];
                for (int i = 0; i < newSchemaIndexesRelativeToOldSchema.length; i++) {
                    if (newTuple[i] == null) {
                        continue;
                    }
                    if (newSchemaIndexesRelativeToOldSchema[i] >= 0) {
                        newAggregatedDatum[i] = newTuple[i];
                    } else {
                        newAggregatedDatum[i] = getNewAggregateSpecificParams(i, newTuple[i]);
                    }
                }
                resultDatumWithUniqueValuesOfGroupByElements.add(newAggregatedDatum);
            }
        }
    }

    public List<Datum[]> getResult() {
        return resultDatumWithUniqueValuesOfGroupByElements;
    }

    private boolean isANewUniqueCombinationOfGroupByElements(int index) {
        return index == resultDatumWithUniqueValuesOfGroupByElements.size();
    }

    private boolean matchesTheCombinationOfGroupByElements(Datum[] newTuple, int index) {
        return tupleComparator.compare(resultDatumWithUniqueValuesOfGroupByElements.get(index), newTuple) == 0;
    }

    private Datum getNewAggregateSpecificParams(int newSchemaIndex, Datum offsetValue) {
        String aggregationName = getAggregationName(newSchemaIndex);
        try {
            switch (aggregationName) {
                case "sum":
                case "SUM":
                    return offsetValue;
                case "count":
                case "COUNT":
                    return new LONG(1l);
                case "avg":
                case "AVG":
                    return new FRACTION(offsetValue.toFLOAT(), 1f);
            }
        } catch (Datum.CastException e) {
            e.printStackTrace();
        }
        throw new UnsupportedOperationException("Unsupported aggregation received " + aggregationName);
    }

    private Datum getUpdatedAggregateSpecificParams(Datum oldDatum, Datum offsetValue, int newColumnIndex) {
        String aggregationName = getAggregationName(newColumnIndex);
        try {
            switch (aggregationName) {
                case "sum":
                case "SUM":
                    return addDatums(oldDatum, offsetValue);
                case "count":
                case "COUNT":
                    return new LONG(oldDatum.toLONG() + 1l);
                case "avg":
                case "AVG":
                    FRACTION fraction = (FRACTION) oldDatum;
                    fraction.setNumerator(fraction.getNumerator() + offsetValue.toFLOAT());
                    fraction.setDenominator(fraction.getDenominator() + 1f);
                    return fraction;
            }
        } catch (Datum.CastException e) {
            e.printStackTrace();
        }
        throw new UnsupportedOperationException("Unsupported aggregation received " + aggregationName);
    }

    private Datum addDatums(Datum oldDatum, Datum offsetValue) {
        try {
            if (oldDatum.getType() == Datum.type.FLOAT) {
                return new FLOAT(oldDatum.toFLOAT() + offsetValue.toFLOAT());
            } else {
                return new LONG(oldDatum.toLONG() + offsetValue.toLONG());
            }
        } catch (Datum.CastException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getAggregationName(int newColumnIndex) {
        return ((Function) newSchema[newColumnIndex].getExpression()).getName();
    }

    private LinkedHashMap<Integer, Boolean> createLinkedHashMapOfIndexes() {
        LinkedHashMap<Integer, Boolean> indexMap = new LinkedHashMap<>();

        for (int i = 0; i < newSchemaIndexesRelativeToOldSchema.length; i++) {
            if (newSchemaIndexesRelativeToOldSchema[i] >= 0) {
                indexMap.put(i, true);
            }
        }
        return indexMap;
    }

    private Datum[] convertTupleToNewSchema(Datum[] oldDatum) {
        Datum[] newDatum = new Datum[newSchemaIndexesRelativeToOldSchema.length];

        for (int i = 0; i < newSchemaIndexesRelativeToOldSchema.length; i++) {
            if (newSchemaIndexesRelativeToOldSchema[i].equals(INDEX_INDICATING_FUNCTION)) {
                newDatum[i] = evaluateExpression(oldDatum, getExpression(i));
            } else if (newSchemaIndexesRelativeToOldSchema[i].equals(INDEX_INDICATING_EXPRESSION_INSIDE_FUNCTION)) {
                newDatum[i] = evaluateExpression(oldDatum, getExecutableExpressionInsideFunction(i));
            } else {
                newDatum[i] = oldDatum[newSchemaIndexesRelativeToOldSchema[i]];
            }
        } return newDatum;
    }

    private Expression getExecutableExpressionInsideFunction(int index) {
        return (Expression)((Function)newSchema[index].getExpression()).getParameters().getExpressions().get(0);
    }

    private Expression getExpression(int i) {
        Function function = (Function) newSchema[i].getExpression();
        if (function.getParameters() != null) {
            Expression expression = (Expression) function.getParameters().getExpressions().get(0);
            return getExecutableExpression(expression);
        }
        return null; //handling count(*)
    }

    private Expression getExecutableExpression(Expression expression) {
        final String expressionString = expression.toString();
        for (ColumnSchema colSchema : oldSchema) {
            if (expressionString.equals(colSchema.getColName()) || expressionString.equals(colSchema.getFullQualifiedName())) {
                return expression;
            } else if (expressionString.equals(colSchema.getColumnAlias())) {
                return colSchema.getExpression();
            }
        }
        throw new UnsupportedOperationException("No executable expression found");
    }

    private Datum evaluateExpression(Datum[] oldDatum, Expression expression) {
        if (expression == null) {
            return new LONG(1l);
        }
        EvaluatorAggregate evalAggregate = new EvaluatorAggregate(oldDatum, oldSchema, expression);
        Datum floatDatum = null;
        try {
            floatDatum = evalAggregate.executeStack();
        } catch (Datum.CastException e) {
            e.printStackTrace();
        }
        return floatDatum;
    }
}

