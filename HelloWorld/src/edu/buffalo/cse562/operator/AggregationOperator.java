package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.data.FLOAT;
import edu.buffalo.cse562.model.aggregation.Aggregation;
import edu.buffalo.cse562.model.aggregation.factory.AggregationObjectFactory;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.schema.Column;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AggregationOperator implements Operator {

    private List<Datum[]> result;
    private Iterator<Datum[]> resultIterator;

    public AggregationOperator(Operator in, ColumnSchema[] newSchema, Integer[] indexArray, List<Function> aggregationFunctions, List<Column> groupByColumnReferences) {
        result = new ArrayList<>();
        pullAllData(aggregationFunctions, in, newSchema, indexArray, groupByColumnReferences);
        reset();
    }

    private void pullAllData(List<Function> aggregationFunctions, Operator in, ColumnSchema[] newSchema, Integer[] indexArray, List<Column> groupByColumnReferences) {
        List<Integer> indexesOfGroupByReferencesInOldSchema = getListOfIndexesOfGroupByReferencesInOldSchema(in.getSchema(), groupByColumnReferences);
        List<Aggregation> aggregations = createAggregations(aggregationFunctions, in, indexesOfGroupByReferencesInOldSchema);
        Datum[] lastProcessedDatum = processAggregatesAndGetLastProcessedDatum(aggregations, in);
        createNewResultSet(aggregations, indexArray, newSchema, lastProcessedDatum);
    }

    private List<Integer> getListOfIndexesOfGroupByReferencesInOldSchema(ColumnSchema[] schema, List<Column> groupByColumnReferences) {
        List<Integer> groupByReferenceIndexes = new ArrayList<>();
        for (Column groupByColumnReference : groupByColumnReferences) {
            for (int i = 0; i < schema.length; i++) {
                if (schema[i].getColName().equalsIgnoreCase(groupByColumnReference.getColumnName())) {
                    groupByReferenceIndexes.add(i);
                    break;
                }
            }
        }
        return groupByReferenceIndexes;
    }

    private void createNewResultSet(List<Aggregation> aggregations, Integer[] indexArray, ColumnSchema[] newSchema, Datum[] lastProcessedDatum) {
        Iterator<Aggregation> aggregationIterator = aggregations.iterator();
        Datum[] resultTuple = new Datum[indexArray.length];
        Datum newColumn;
        for (int i = 0; i < indexArray.length; i++) {
            if (indexArray[i] >= 0) {
                newColumn = lastProcessedDatum[indexArray[i]];
            } else {
                newColumn = new FLOAT(aggregationIterator.next().getValue());
            }
            resultTuple[i] = newColumn;
        }
        result.add(resultTuple);
    }

    private Datum[] processAggregatesAndGetLastProcessedDatum(List<Aggregation> aggregations, Operator in) {
        Datum[] datum;
        Datum[] lastProcessedDatum = null;
        while ((datum = in.readOneTuple()) != null) {
            for (Aggregation aggregation : aggregations) {
                aggregation.process(datum);
            }
            lastProcessedDatum = datum;
        }
        return lastProcessedDatum;
    }

    private List<Aggregation> createAggregations(List<Function> aggregationFunctions, Operator in, List<Integer> indexesOfGroupByReferencesInOldSchema) {
        List<Aggregation> aggregations = new ArrayList<>();
        Aggregation aggregation;
        for (Function aggregationFunction : aggregationFunctions) {
            aggregation = AggregationObjectFactory.getAggregationObject(aggregationFunction, in.getSchema(), indexesOfGroupByReferencesInOldSchema);
            aggregations.add(aggregation);
        }
        return aggregations;
    }

    @Override
    public Datum[] readOneTuple() {
        if (resultIterator.hasNext()) {
            return resultIterator.next();
        }
        return null;
    }

    @Override
    public void reset() {
        resultIterator = result.iterator();
    }

    @Override
    public ColumnSchema[] getSchema() {
        return new ColumnSchema[0];
    }
}
