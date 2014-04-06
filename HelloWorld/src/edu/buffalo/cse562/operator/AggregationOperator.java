package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.comparator.TupleComparator;
import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.model.aggregation.AggregationProcessor;
import edu.buffalo.cse562.schema.ColumnSchema;
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
//        List<Column> distinctOnColumnReferences = plainSelect.getDistinct().getOnSelectItems();

        AggregationProcessor aggregationProcessor = new AggregationProcessor(in.getSchema(), newSchema, indexArray);
        Datum[] datum;
        while ((datum = in.readOneTuple()) != null) {
            aggregationProcessor.process(datum);
        }
        result = aggregationProcessor.getResult();
        if (groupByColumnReferences != null) {
            Collections.sort(result, new TupleComparator(getListOfIndexesOfGroupByReferencesInNewSchema(newSchema, groupByColumnReferences)));
        }
    }

    private LinkedHashMap<Integer, Boolean> getListOfIndexesOfGroupByReferencesInNewSchema(ColumnSchema[] schema, List<Column> groupByColumnReferences) {
        LinkedHashMap<Integer, Boolean> groupByReferenceIndexes = new LinkedHashMap<>();
        for (Column groupByColumnReference : groupByColumnReferences) {
            for (int i = 0; i < schema.length; i++) {
                if (schema[i].matchColumn(groupByColumnReference)) {
                    groupByReferenceIndexes.put(i, true);
                    break;
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
}
