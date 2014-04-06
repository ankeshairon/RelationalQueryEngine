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

import java.util.*;

import static edu.buffalo.cse562.SchemaIndexConstants.*;

public class AggregationProcessor {

    private final ColumnSchema[] oldSchema;
    private final ColumnSchema[] newSchema;

    private List<Datum[]> resultDatumWithUniqueValuesOfGroupByElements;

    // <groupBy columnIndex,  <distinct column index, list of unique values>>>
//    private Map<Integer, Map<Integer, Set<Datum>>> distinctElementsInGroupBy;


    private Map<Integer, Set<Datum>> distinctElementsInGroupBy;
    private Set<Integer> distinctColumnIndexes;

    //array of corresp old schema indexes
    private Integer[] relativeNewSchemaIndexes;
    private TupleComparator tupleComparator;


    public AggregationProcessor(ColumnSchema[] oldSchema, ColumnSchema[] newSchema, Integer[] relativeNewSchemaIndexes) {
        this.oldSchema = oldSchema;
        this.newSchema = newSchema;
        this.relativeNewSchemaIndexes = relativeNewSchemaIndexes;
        tupleComparator = new TupleComparator(createLinkedHashMapOfIndexes());
        resultDatumWithUniqueValuesOfGroupByElements = new ArrayList<>();
        distinctElementsInGroupBy = new HashMap<>();
        createDistinctColumnIndexes();
    }

    private void createDistinctColumnIndexes() {
        distinctColumnIndexes = new TreeSet<>();
//        for (int i = 0; i < oldSchema.length; i++) {
//            if (oldSchema[i].getIsDistinct()) {
        for (int i = 0; i < newSchema.length; i++) {
            if (isSchemaIndexIndicatingFunctionWithoutExpression(relativeNewSchemaIndexes[i])) {
                distinctColumnIndexes.add(relativeNewSchemaIndexes[i]);
            }
        }
    }


    public void process(Datum[] oldTuple) {
        //check if the tuple received has resultDatumWithUniqueValuesOfGroupByElements
        //if yes, then update the corresponding data values
        //else add new and add corresponding data values

        Datum[] newTuple = convertTupleToNewSchema(oldTuple);

        if (!(newTuple == null)) {
            int index;
            for (index = 0; index < resultDatumWithUniqueValuesOfGroupByElements.size(); index++) {
                //compare tuple with saved group by columns
                if (matchesTheCombinationOfGroupByElements(newTuple, index)) {
                    Datum[] aggregatedDatum = resultDatumWithUniqueValuesOfGroupByElements.get(index);
                    updateAggregatedTupleWithValuesFromNewTuple(newTuple, aggregatedDatum);
                    resultDatumWithUniqueValuesOfGroupByElements.set(index, aggregatedDatum);
                    break;
                }
            }
            if (isANewUniqueCombinationOfGroupByElements(index)) {
                Datum[] newAggregatedDatum = new Datum[newSchema.length];
                for (int i = 0; i < relativeNewSchemaIndexes.length; i++) {
//                    if (newTuple[i] == null) {
//                        continue;
//                    }
                    if (relativeNewSchemaIndexes[i] >= 0) {
                        newAggregatedDatum[i] = newTuple[i];
                    } else {
                        newAggregatedDatum[i] = getNewAggregateSpecificParams(i, newTuple[i]);
                    }

                    if (distinctColumnIndexes.contains(relativeNewSchemaIndexes[i])) {
                        distinctElementsInGroupBy.put(relativeNewSchemaIndexes[i], newDistinctColumnValuesSet(i, newTuple[i]));
                    }
                }
                resultDatumWithUniqueValuesOfGroupByElements.add(newAggregatedDatum);
            }
        }
    }

    private void updateAggregatedTupleWithValuesFromNewTuple(Datum[] newTuple, Datum[] aggregatedTuple) {
        //iterate over each cell to update corresponding cell
        for (int i = 0; i < relativeNewSchemaIndexes.length; i++) {
//                        if (newTuple[i] == null) {
//                            continue;
//                        }
            if (relativeNewSchemaIndexes[i] >= 0) {
                aggregatedTuple[i] = newTuple[i];
            } else {
                aggregatedTuple[i] = getUpdatedAggregateSpecificParams(aggregatedTuple[i], newTuple[i], i);
            }
        }
    }

    private Set<Datum> newDistinctColumnValuesSet(Integer index, Datum datum) {
        final Set<Datum> distinctColumnValuesSet = new HashSet<>();
        distinctColumnValuesSet.add(datum);

//        final HashMap<Integer, Set<Datum>> distinctColumnValuesMap = new HashMap<>();
//        distinctColumnValuesMap.put(index, distinctColumnValuesSet);
        return distinctColumnValuesSet;
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
        if (distinctColumnIndexes.contains(getSchemaIndexForFunctionWithoutExpression(newColumnIndex))) {
            return getUpdatedAggregateSpecificDistinctParams(aggregationName, oldDatum, newColumnIndex);
        } else {
            return getUpdatedAggregateSpecificNonDistinctParams(oldDatum, offsetValue, aggregationName);
        }
    }

    private Datum getUpdatedAggregateSpecificNonDistinctParams(Datum oldDatum, Datum offsetValue, String aggregationName) {
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

    private Datum getUpdatedAggregateSpecificDistinctParams(String aggregationName, Datum oldDatum, int newColumnIndex) {
        //  <distinct groupBy columnIndex, list of unique values>>>
//         Map<Integer, Set<Datum>> distinctElementsInGroupBy;
        final int distinctColumnIndex = getSchemaIndexForFunctionWithoutExpression(newColumnIndex);
        distinctElementsInGroupBy.get(distinctColumnIndex).add(oldDatum);

        if (aggregationName.equals("count") || aggregationName.equals("COUNT")) {
            return new LONG(distinctElementsInGroupBy.get(distinctColumnIndex).size());
        }

        throw new UnsupportedOperationException("Unsupported aggregation received with distinct : " + aggregationName);
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

        for (int i = 0; i < relativeNewSchemaIndexes.length; i++) {
            if (relativeNewSchemaIndexes[i] >= 0) {
                indexMap.put(i, true);
            }
        }
        return indexMap;
    }

    private Datum[] convertTupleToNewSchema(Datum[] oldDatum) {
        Datum[] newDatum = new Datum[relativeNewSchemaIndexes.length];

        for (int i = 0; i < relativeNewSchemaIndexes.length; i++) {
            if (isSchemaIndexIndicatingFunctionWithoutExpression(relativeNewSchemaIndexes[i])) {
                newDatum[i] = evaluateExpression(oldDatum, getExpression(i));
            } else if (relativeNewSchemaIndexes[i].equals(SCHEMA_INDEX_INDICATING_EXPRESSION_INSIDE_FUNCTION)) {
                newDatum[i] = evaluateExpression(oldDatum, getExecutableExpressionInsideFunction(i));
            } else {
                newDatum[i] = oldDatum[relativeNewSchemaIndexes[i]];
            }
        }
        return newDatum;
    }

    private Expression getExecutableExpressionInsideFunction(int index) {
        return (Expression) ((Function) newSchema[index].getExpression()).getParameters().getExpressions().get(0);
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

