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

    private List<AggregationTuple> aggregationTupleList;
    private List<Integer> distinctColumnIndexes;

    //array of corresp old schema indexes
    private Integer[] relativeNewSchemaIndexes;
    private TupleComparator groupByComparator;

    private Boolean isGroupByPresent;

    private Map<Integer, String> aggregationNameAt;
    private Map<Integer, Expression> aggregationExpressionAt;

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
        aggregationExpressionAt = new HashMap<>();

        for (int i = 0; i < relativeNewSchemaIndexes.length; i++) {
            if (relativeNewSchemaIndexes[i] < 0) {
                aggregationNameAt.put(i, ((Function) newSchema[i].getExpression()).getName());
            }
            if (relativeNewSchemaIndexes[i].equals(SCHEMA_INDEX_INDICATING_EXPRESSION_INSIDE_FUNCTION)) {
                aggregationExpressionAt.put(i, ((Expression) ((Function) newSchema[i].getExpression()).getParameters().getExpressions().get(0)));
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
//                distinctColumnIndexes.add(relativeNewSchemaIndexes[i]);
                distinctColumnIndexes.add(i);
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
        int i = isGroupByPresent ? aggregationTupleList.indexOf(new AggregationTuple(newRawTuple, groupByComparator)) : 0;

        if (i != -1) {
            aggregationTuple = aggregationTupleList.get(i);
            updateAggregatedTupleWithValuesFromNewTuple(newRawTuple, aggregationTuple);
        } else {
            Datum[] newAggregatedDatum = createNewAggregatedDatum(newRawTuple);
            aggregationTuple = new AggregationTuple(newAggregatedDatum, groupByComparator, distinctColumnIndexes);
            aggregationTuple.updateDistinctElementsSet(oldTuple);
            aggregationTupleList.add(aggregationTuple);
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
                    aggregationTuple.addToDistinctElementsSet(getOldColumnIndexReferencedByFunction (relativeNewSchemaIndexes[i]), newTuple[i]);
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
        for (AggregationTuple aggregationTuple : aggregationTupleList) {
            result.add(aggregationTuple.getUnderlyingConsolidatedTuple());
        }

        Collections.sort(result, groupByComparator);

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
                    return new FRACTION(offsetValue.toFLOAT(), 1f);
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
                    fraction.setNumerator(fraction.getNumerator() + offsetValue.toFLOAT());
                    fraction.setDenominator(fraction.getDenominator() + 1f);
                    return fraction;
            }
        } catch (Datum.CastException e) {
            e.printStackTrace();
        }
        throw new UnsupportedOperationException("Unsupported aggregation received " + aggregationName);
    }

    private Datum getUpdatedAggregateSpecificDistinctParams(String aggregationName, AggregationTuple aggregationTuple, int index) {
        int oldColumnIndex = getOldColumnIndexReferencedByFunction(index);
        if (aggregationName.equals("count") || aggregationName.equals("COUNT")) {
            return new LONG(aggregationTuple.getNoOfDistinctValuesOfColumnWithIndex(oldColumnIndex));
        }
        throw new UnsupportedOperationException("Unsupported aggregation received with distinct " + aggregationName);
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

    private Datum[] convertTupleToNewSchema(Datum[] oldDatum) {
        Datum[] newDatum = new Datum[relativeNewSchemaIndexes.length];

        for (int i = 0; i < relativeNewSchemaIndexes.length; i++) {
            if (isSchemaIndexIndicatingFunctionWithoutExpression(relativeNewSchemaIndexes[i])) {
                newDatum[i] = evaluateExpression(oldDatum, getExpression(i));
            } else if (relativeNewSchemaIndexes[i].equals(SCHEMA_INDEX_INDICATING_EXPRESSION_INSIDE_FUNCTION)) {
                newDatum[i] = evaluateExpression(oldDatum, aggregationExpressionAt.get(i));
            } else {
                newDatum[i] = oldDatum[relativeNewSchemaIndexes[i]];
            }
        }
        return newDatum;
    }

    private Expression getExpression(int i) {
        Function function = (Function) newSchema[i].getExpression();
        if (function.getParameters() != null) {
            Expression expression = (Expression) function.getParameters().getExpressions().get(0);
            return getExecutableExpression(expression, i);
        }
        return null; //handling count(*)
    }

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