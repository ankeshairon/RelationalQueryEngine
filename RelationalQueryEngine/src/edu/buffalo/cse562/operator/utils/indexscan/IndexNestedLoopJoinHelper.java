package edu.buffalo.cse562.operator.utils.indexscan;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.indexer.service.IndexService;
import edu.buffalo.cse562.indexer.service.IndexedDataMap;
import edu.buffalo.cse562.model.Pair;
import edu.buffalo.cse562.operator.joins.IndexNestedLoopJoinOperator;
import edu.buffalo.cse562.schema.ColumnSchema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IndexNestedLoopJoinHelper {
    private final List<Pair<Long, Long>> resultantRowIdPairs;
    private final IndexedDataMap indexedDataMap1;
    private final IndexedDataMap indexedDataMap2;

    final IndexService indexService;
    private final String tblName1;
    private final String tblName2;

    public IndexNestedLoopJoinHelper(ColumnSchema[] schema1, ColumnSchema[] schema2, Integer position1, Integer position2) {
        tblName1 = schema1[0].getTblName();
        tblName2 = schema2[0].getTblName();

        indexService = IndexService.getInstance();
        indexedDataMap1 = indexService.getIndexedDataFor(tblName1, schema1, position1);
        indexedDataMap2 = indexService.getIndexedDataFor(tblName2, schema2, position2);
        resultantRowIdPairs = new ArrayList<>();
        evaluate();
    }

    private void evaluate() {
        final Iterator<Datum> iterator1 = indexedDataMap1.getAllSecondaryKeys().iterator();
        final Iterator<Datum> iterator2 = indexedDataMap2.getAllSecondaryKeys().iterator();
        Datum key1 = iterator1.next();
        Datum key2 = iterator2.next();
        int comparisonResult;

        do {
//            comparisonResult = key1.compareTo(key2);
//            if (comparisonResult == 0) {
            addAllRowIdPairsForKeys(key1, key2);
            key1 = iterator1.next();
            key2 = iterator2.next();
//            } else if (comparisonResult < 0) {
//                key1 = iterator1.next();
//            } else if (comparisonResult > 0) {
//                key2 = iterator2.next();
//            } else {
//
//            }
        }
        while (iterator1.hasNext() && iterator2.hasNext());
    }

    private void addAllRowIdPairsForKeys(Datum key1, Datum key2) {
        final List<Long> rowIds1 = indexedDataMap1.getRowIdsForKey(key1);
        final List<Long> rowIds2 = indexedDataMap2.getRowIdsForKey(key2);

        for (Long rowId1 : rowIds1) {
            for (Long rowId2 : rowIds2) {
                resultantRowIdPairs.add(new Pair<>(rowId1, rowId2));
            }
        }
    }

    public void populateFields(IndexNestedLoopJoinOperator iNLJOperator) {
        iNLJOperator.setStoreMap1(indexService.getPrimaryStoreMap(tblName1));
        iNLJOperator.setStoreMap2(indexService.getPrimaryStoreMap(tblName2));
        iNLJOperator.setRowIdPairsIterator(resultantRowIdPairs.iterator());
    }
}
