package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.operator.abstractoperators.Operator;
import edu.buffalo.cse562.operator.utils.aggregation.AggregationProcessor;
import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.visitor.optimizer.ObjectSizer;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.util.*;

public class AggregationOperator implements Operator {

    private final ColumnSchema[] newSchema;
    private List<Datum[]> result;
    private Iterator<Datum[]> resultIterator;

    public AggregationOperator(Operator in, ColumnSchema[] newSchema, Integer[] indexArray, PlainSelect plainSelect) {
        this.newSchema = newSchema;
        result = new ArrayList<>();
        pullAllData(in, newSchema, indexArray, plainSelect);
        reset();
    }

    private void pullAllData(Operator in, ColumnSchema[] newSchema, Integer[] indexArray, PlainSelect plainSelect) {
        List<Column> groupByColumnReferences = plainSelect.getGroupByColumnReferences();
        final LinkedHashMap<Integer, Boolean> groupByColumnIndexes = getGroupByColumnIndexes(in.getSchema(), groupByColumnReferences, Arrays.asList(indexArray));

        AggregationProcessor aggregationProcessor = new AggregationProcessor(in.getSchema(), newSchema, indexArray, groupByColumnIndexes);
        Datum[] datum;
        while ((datum = in.readOneTuple()) != null) {
            aggregationProcessor.process(datum);
        }
        result = aggregationProcessor.getResult();
    }

    private LinkedHashMap<Integer, Boolean> getGroupByColumnIndexes(ColumnSchema[] schema, List<Column> groupByColumnReferences, List<Integer> indexArray) {
        LinkedHashMap<Integer, Boolean> groupByReferenceIndexes = new LinkedHashMap<>();
        if (groupByColumnReferences != null) {
            for (Column groupByColumnReference : groupByColumnReferences) {
                for (int i = 0; i < schema.length; i++) {
                    if (schema[i].matchColumn(groupByColumnReference)) {
                        groupByReferenceIndexes.put(indexArray.indexOf(i), true);
                        break;
                    }
                }
            }
        }
        return groupByReferenceIndexes;
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
        return newSchema;
    }

    @Override
    public Long getProbableTableSize() {
        return ObjectSizer.getObjectSize(result);
    }
}
