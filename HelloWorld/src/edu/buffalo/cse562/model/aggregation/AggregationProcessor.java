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

public class AggregationProcessor {

    private final ColumnSchema[] oldSchema;
    private final ColumnSchema[] newSchema;

    private List<Datum[]> resultDatumWithUniqueValuesOfGroupByElements;

    private List<Integer> indexesOfGroupByReferencesInOldSchema;
    private Integer[] newSchemaIndexesRelativeToOldSchema;
    private TupleComparator tupleComparator;


    public AggregationProcessor(ColumnSchema[] oldSchema, ColumnSchema[] newSchema, List<Integer> indexesOfGroupByReferencesInOldSchema, Integer[] newSchemaIndexesRelativeToOldSchema) {
        this.oldSchema = oldSchema;
        this.newSchema = newSchema;
        this.indexesOfGroupByReferencesInOldSchema = indexesOfGroupByReferencesInOldSchema;
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
                if (tupleComparator.compare(resultDatumWithUniqueValuesOfGroupByElements.get(index), newTuple) == 0) {
                    Datum[] newAggregatedDatum = resultDatumWithUniqueValuesOfGroupByElements.get(index);
                    for (int i = 0; i < newSchemaIndexesRelativeToOldSchema.length; i++) {
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
            if (index == resultDatumWithUniqueValuesOfGroupByElements.size()) {
                Datum[] newAggregatedDatum = new Datum[newSchema.length];
                for (int i = 0; i < newSchemaIndexesRelativeToOldSchema.length; i++) {
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
        for (Integer i : indexesOfGroupByReferencesInOldSchema) {
            indexMap.put(i, true);
        }
        return indexMap;
    }

    private Datum[] convertTupleToNewSchema(Datum[] oldDatum) {
        //todo call Dev's code to evaluate expressions life "valueof(Field1) + valueof(Field2)"
        Datum[] newDatum = new Datum[newSchemaIndexesRelativeToOldSchema.length];

        for (int i = 0; i < newSchemaIndexesRelativeToOldSchema.length; i++) {
            if (newSchemaIndexesRelativeToOldSchema[i] >= 0) {
                newDatum[i] = oldDatum[newSchemaIndexesRelativeToOldSchema[i]];
            } else {
                newDatum[i] = functionToCall(oldDatum, oldSchema, newSchema[i].getExpression());
            }
            if (newDatum[i] == null) {
                return null;
            }
        }
        return newDatum;
    }

    private Datum functionToCall(Datum[] oldDatum, ColumnSchema[] oldSchema, Expression expression) {
        EvaluatorAggregate evalAggregate = new EvaluatorAggregate(oldDatum, oldSchema, expression);
        expression.accept(evalAggregate);
        Datum floatDatum = null;
        try {
            floatDatum = evalAggregate.executeStack();
        } catch (Datum.CastException e) {
            e.printStackTrace();
        }
        return floatDatum;
    }
}

