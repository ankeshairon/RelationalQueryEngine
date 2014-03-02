package edu.buffalo.cse562.model.aggregation;

import edu.buffalo.cse562.comparator.TupleComparator;
import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.data.FLOAT;
import edu.buffalo.cse562.data.FRACTION;
import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.visitor.EvaluatorAggregate;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
        //if yes, then update the corresponding params
        //else add new and add corresponding params

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

    private boolean isANewUniqueCombinationOfGroupByElements(int index) {
        return index == resultDatumWithUniqueValuesOfGroupByElements.size();
    }

    private boolean matchesTheCombinationOfGroupByElements(Datum[] newTuple, int index) {
        return tupleComparator.compare(resultDatumWithUniqueValuesOfGroupByElements.get(index), newTuple) == 0;
    }

    public List<Datum[]> getResult() {
        return resultDatumWithUniqueValuesOfGroupByElements;
    }

    private Datum getNewAggregateSpecificParams(int newSchemaIndex, Datum offsetValue) {
        String aggregationName = getAggregationName(newSchemaIndex);
        try {
            switch (aggregationName) {
                case "sum":
                case "SUM":
                    return new FLOAT(offsetValue.toFLOAT());
                case "count":
                case "COUNT":
                    return new FLOAT(1f);
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
                    return new FLOAT(oldDatum.toFLOAT() + offsetValue.toFLOAT());
                case "count":
                case "COUNT":
                    return new FLOAT(oldDatum.toFLOAT() + 1f);
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
            if (newSchemaIndexesRelativeToOldSchema[i] == INDEX_INDICATING_FUNCTION) {
                newDatum[i] = evaluateExpression(oldDatum, oldSchema, getExpression(i));
            } else {
                newDatum[i] = oldDatum[newSchemaIndexesRelativeToOldSchema[i]];
            }
        }
        return newDatum;
    }

    private Expression getExpression(int i) {
        Function function = (Function) newSchema[i].getExpression();
        if (function.getParameters() != null) {
            return (Expression) function.getParameters().getExpressions().get(0);
        }
        return null; //handling count(*)
    }

    private Datum evaluateExpression(Datum[] oldDatum, ColumnSchema[] oldSchema, Expression expression) {
        if (expression == null) {
            return new FLOAT(1);
        }
        EvaluatorAggregate evalAggregate = new EvaluatorAggregate(oldDatum, oldSchema, expression);
        expression.accept(evalAggregate);
        //evalAggregate.showStack();
        Datum floatDatum = null;
        try {
            floatDatum = evalAggregate.executeStack();
        } catch (Datum.CastException e) {
            e.printStackTrace();
        }
        return floatDatum;
    }
}

