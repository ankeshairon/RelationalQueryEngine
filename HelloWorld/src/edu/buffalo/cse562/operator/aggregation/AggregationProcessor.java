package edu.buffalo.cse562.operator.aggregation;

import edu.buffalo.cse562.comparator.TupleComparator;
import edu.buffalo.cse562.data.DOUBLE;
import edu.buffalo.cse562.data.Datum;
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

    private AggregationTuple singleAggregationTuple;

    private List<AggregationTuple> aggregationTupleList;
    private List<Integer> distinctColumnIndexes;

    //array of corresp old oldSchema indexes
    private Integer[] relativeNewSchemaIndexes;
    private TupleComparator groupByComparator;

    private Boolean isGroupByPresent;

    private Map<Integer, String> aggregationNameAt;
    private Map<Integer, EvaluatorAggregate> aggregationEvaluators;

    public AggregationProcessor(ColumnSchema[] oldSchema, ColumnSchema[] newSchema, Integer[] relativeNewSchemaIndexesGroupBy, LinkedHashMap<Integer, Boolean> groupByColumnIndexes) {
        isGroupByPresent = groupByColumnIndexes.size() != 0;
        this.oldSchema = oldSchema;
        this.newSchema = newSchema;
        this.relativeNewSchemaIndexes = relativeNewSchemaIndexesGroupBy;
        groupByComparator = new TupleComparator(groupByColumnIndexes);
        distinctColumnIndexes = createDistinctColumnIndexes();
        aggregationTupleList = createAggregationTupleList();
        createAggregationNamesAndExpressionsMap();
    }

    private void createAggregationNamesAndExpressionsMap() {
        aggregationNameAt = new HashMap<>();
        aggregationEvaluators = new HashMap<>();

        for (int i = 0; i < relativeNewSchemaIndexes.length; i++) {
            if (relativeNewSchemaIndexes[i] < 0) {
                aggregationNameAt.put(i, ((Function) newSchema[i].getExpression()).getName());
            }
            if (relativeNewSchemaIndexes[i].equals(SCHEMA_INDEX_INDICATING_EXPRESSION)) {
                aggregationEvaluators.put(i, new EvaluatorAggregate(oldSchema, newSchema[i].getExpression()));
            } else if (isExpressionInsideFunction(relativeNewSchemaIndexes[i])) {
                final Expression expression = (Expression) ((Function) newSchema[i].getExpression()).getParameters().getExpressions().get(0);
                aggregationEvaluators.put(i, new EvaluatorAggregate(oldSchema, getExecutableExpression(expression, i)));
            }
        }
    }

    private ArrayList<AggregationTuple> createAggregationTupleList() {
        final ArrayList<AggregationTuple> aggregationTuples = new ArrayList<>();
        if (!isGroupByPresent) {
            aggregationTuples.add(new AggregationTuple(new Datum[newSchema.length], groupByComparator, distinctColumnIndexes));
        }
        return aggregationTuples;
    }

    private List<Integer> createDistinctColumnIndexes() {
        List<Integer> distinctColumnIndexes = new ArrayList<>();
        for (int i = 0; i < newSchema.length; i++) {
            if (newSchema[i].isDistinct() != null && newSchema[i].isDistinct()) {
                distinctColumnIndexes.add(relativeNewSchemaIndexes[i]);
            }
        }
        return distinctColumnIndexes;
    }


    public void process(Datum[] oldTuple) {
        //check if the tuple received has aggregationTupleList
        //if yes, then update the corresponding data values
        //else add new and add corresponding data values

        Datum[] newRawTuple = convertTupleToNewSchema(oldTuple);

        AggregationTuple aggregationTuple;

        if (isGroupByPresent) {
            int i = aggregationTupleList.indexOf(new AggregationTuple(newRawTuple, groupByComparator));
            if (i == -1) {
                Datum[] newAggregatedDatum = createNewAggregatedDatum(newRawTuple);
                aggregationTuple = new AggregationTuple(newAggregatedDatum, groupByComparator, distinctColumnIndexes);
                aggregationTuple.updateDistinctElementsSet(oldTuple);
                aggregationTupleList.add(aggregationTuple);
            } else {
                aggregationTuple = aggregationTupleList.get(i);
                updateAggregatedTupleWithValuesFromNewTuple(newRawTuple, aggregationTuple);
            }
        } else {
            if (singleAggregationTuple == null) {
                singleAggregationTuple = new AggregationTuple(newRawTuple, groupByComparator);
            } else {
                updateAggregatedTupleWithValuesFromNewTuple(newRawTuple, singleAggregationTuple);
            }
        }
    }

    private Datum[] createNewAggregatedDatum(Datum[] newTuple) {
        int i;
        Datum[] newAggregatedDatum = new Datum[newSchema.length];

        for (i = 0; i < relativeNewSchemaIndexes.length; i++) {
            if (relativeNewSchemaIndexes[i] >= 0) {
                newAggregatedDatum[i] = newTuple[i];
            } else {
                newAggregatedDatum[i] = getNewAggregateSpecificParams(i, newTuple[i]);
            }
        }
        return newAggregatedDatum;
    }

    private void updateAggregatedTupleWithValuesFromNewTuple(Datum[] newTuple, AggregationTuple aggregationTuple) {
        final Datum[] aggregatedTuple = aggregationTuple.getUnderlyingConsolidatedTuple();
        //iterate over each cell to update corresponding cell
        for (int i = 0; i < relativeNewSchemaIndexes.length; i++) {
            if (relativeNewSchemaIndexes[i] >= 0) {
                aggregatedTuple[i] = newTuple[i];
            } else {
                Datum result;
                String aggregationName = aggregationNameAt.get(i);
                if (newSchema[i].isDistinct()) {
                    aggregationTuple.addToDistinctElementsSet(getOldIndexReferencedByFunction(relativeNewSchemaIndexes[i]), newTuple[i]);
                    result = getUpdatedAggregateSpecificDistinctParams(aggregationName, aggregationTuple, relativeNewSchemaIndexes[i]);
                } else {
                    result = getUpdatedAggregateSpecificNonDistinctParams(aggregationName, aggregatedTuple[i], newTuple[i]);
                }
                aggregatedTuple[i] = result;
            }
        }
    }

    public List<Datum[]> getResult() {
        List<Datum[]> result = new ArrayList<>();

        if (singleAggregationTuple == null) {
            for (AggregationTuple aggregationTuple : aggregationTupleList) {
                result.add(aggregationTuple.getUnderlyingConsolidatedTuple());
            }
            Collections.sort(result, groupByComparator);
        } else {
            result.add(singleAggregationTuple.getUnderlyingConsolidatedTuple());
        }
        return result;
    }

    private Datum getNewAggregateSpecificParams(int newSchemaIndex, Datum offsetValue) {
        String aggregationName = aggregationNameAt.get(newSchemaIndex);
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
                    return new FRACTION(offsetValue.toDOUBLE(), 1d);
            }
        } catch (Datum.CastException e) {
            e.printStackTrace();
        }
        throw new UnsupportedOperationException("Unsupported aggregation received " + aggregationName);
    }

    private Datum getUpdatedAggregateSpecificNonDistinctParams(String aggregationName, Datum oldDatum, Datum offsetValue) {
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
                    fraction.setNumerator(fraction.getNumerator() + offsetValue.toDOUBLE());
                    fraction.setDenominator(fraction.getDenominator() + 1d);
                    return fraction;
            }
        } catch (Datum.CastException e) {
            e.printStackTrace();
        }
        throw new UnsupportedOperationException("Unsupported aggregation received " + aggregationName);
    }

    private Datum getUpdatedAggregateSpecificDistinctParams(String aggregationName, AggregationTuple aggregationTuple, int index) {
        int oldColumnIndex = getOldIndexReferencedByFunction(index);
        if (aggregationName.equals("count") || aggregationName.equals("COUNT")) {
            return new LONG(aggregationTuple.getNoOfDistinctValuesOfColumnWithIndex(oldColumnIndex));
        }
        throw new UnsupportedOperationException("Unsupported aggregation received with distinct " + aggregationName);
    }

    private Datum addDatums(Datum oldDatum, Datum offsetValue) {
        try {
            if (oldDatum.getType() == Datum.type.DOUBLE) {
                return new DOUBLE(oldDatum.toDOUBLE() + offsetValue.toDOUBLE());
            } else {
                return new LONG(oldDatum.toLONG() + offsetValue.toLONG());
            }
        } catch (Datum.CastException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Datum[] convertTupleToNewSchema(Datum[] oldDatum) {
        Datum[] newDatum = new Datum[relativeNewSchemaIndexes.length];

        for (int i = 0; i < relativeNewSchemaIndexes.length; i++) {
            if (isExpressionInsideFunction(relativeNewSchemaIndexes[i])) {
                newDatum[i] = aggregationEvaluators.get(i).executeStack(oldDatum);
            } else if (isStarInsideFunction(relativeNewSchemaIndexes[i])) {
                newDatum[i] = getNewAggregateSpecificParams(i, null);
            } else {
                newDatum[i] = oldDatum[relativeNewSchemaIndexes[i]];
            }
        }
        return newDatum;
    }

//    private Expression getExpression(int i) {
//        Function function = (Function) newSchema[i].getExpression();
//        if (function.getParameters() != null) {
//            Expression expression = (Expression) function.getParameters().getExpressions().get(0);
//            return getExecutableExpression(expression, i);
//        }
//        return null; //handling count(*)
//    }

    private Expression getExecutableExpression(Expression expression, int i) {
        final String expressionString = expression.toString();
        ColumnSchema colSchema = newSchema[i];
        if (colSchema.matchColumnNameOnly(expressionString) || colSchema.matchFullQualifiedName(expressionString)) {
            return expression;
        } else if (expressionString.equals(colSchema.getColumnAlias())) {
            return colSchema.getExpression();
        } else if (colSchema.getExpression() != null && ((Function) colSchema.getExpression()).getParameters().getExpressions().get(0).equals(expression)) {
            return expression;
        }
        throw new UnsupportedOperationException("No executable expression found");
    }
}