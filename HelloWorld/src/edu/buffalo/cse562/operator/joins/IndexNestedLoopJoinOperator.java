package edu.buffalo.cse562.operator.joins;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.model.Pair;
import edu.buffalo.cse562.operator.abstractoperators.FilterOperator;
import edu.buffalo.cse562.operator.abstractoperators.JoinOperator;
import edu.buffalo.cse562.operator.utils.indexscan.IndexNestedLoopJoinHelper;
import edu.buffalo.cse562.schema.ColumnSchema;
import jdbm.PrimaryStoreMap;

import java.util.Iterator;
import java.util.List;

import static edu.buffalo.cse562.operator.utils.scan.ScanUtils.getDataForRelevantColumnIndexes;

public class IndexNestedLoopJoinOperator extends JoinOperator {

    private PrimaryStoreMap<Long, String> storeMap1;
    private PrimaryStoreMap<Long, String> storeMap2;

    private final List<Integer> relevantColumnIndexes1;
    private final List<Integer> relevantColumnIndexes2;

    private final ColumnSchema[] schema1;
    private final ColumnSchema[] schema2;

    private Iterator<Pair<Long, Long>> rowIdPairsIterator;
    private Integer newTupleSize1;
    private Integer newTupleSize2;


    public IndexNestedLoopJoinOperator(FilterOperator operator1, FilterOperator operator2, Integer position1, Integer position2) {
        super(operator1, operator2);
        schema1 = operator1.getSchema();
        schema2 = operator2.getSchema();
        newTupleSize1 = schema1.length;
        newTupleSize2 = schema2.length;
        relevantColumnIndexes1 = operator1.getRelevantColumnIndexes();
        relevantColumnIndexes2 = operator2.getRelevantColumnIndexes();
        IndexNestedLoopJoinHelper helper = new IndexNestedLoopJoinHelper(operator1.getSchema(), operator2.getSchema(), position1, position2);
        helper.populateFields(this);
    }

    @Override
    public Datum[] readOneTuple() {
        if (!rowIdPairsIterator.hasNext()) {
            return null;
        }

        final Pair<Long, Long> rowIdPair = rowIdPairsIterator.next();
        Datum[] newtuple = new Datum[newTupleSize1 + newTupleSize2];

        Datum[] tuple1 = getDataForRelevantColumnIndexes(rowIdPair.getFirst(), relevantColumnIndexes1, storeMap1, schema1);
        Datum[] tuple2 = getDataForRelevantColumnIndexes(rowIdPair.getSecond(), relevantColumnIndexes2, storeMap2, schema2);

        System.arraycopy(tuple1, 0, newtuple, 0, newTupleSize1);
        System.arraycopy(tuple2, 0, newtuple, newTupleSize1, newTupleSize2);

        return newtuple;
    }

    @Override
    public void reset() {

    }

    public void setRowIdPairsIterator(Iterator<Pair<Long, Long>> rowIdPairsIterator) {
        this.rowIdPairsIterator = rowIdPairsIterator;
    }

    public void setStoreMap1(PrimaryStoreMap<Long, String> storeMap1) {
        this.storeMap1 = storeMap1;
    }

    public void setStoreMap2(PrimaryStoreMap<Long, String> storeMap2) {
        this.storeMap2 = storeMap2;
    }
}
