package edu.buffalo.cse562.indexer.service;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.indexer.modifier.Indexer;
import edu.buffalo.cse562.schema.ColumnSchema;
import jdbm.PrimaryStoreMap;
import jdbm.SecondaryTreeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class interacts directly with PrimaryStoreMap & has methods not requiring any column names (SecondaryIndexes)
 */
public class IndexService extends Indexer {

    private static IndexService indexService;

    public IndexService(File indexDir) {
        super(indexDir);
    }

    /**
     * Input param - tableName, input schema, position of indexed column in schema
     * returns Map<Datum, List<<String>> where Datum is the key on which it is indexed & List<String> is the list of raw tuples
     */
    public IndexedDataMap getIndexedDataFor(String tableName, ColumnSchema[] schema, Integer columnPosition) {

        final PrimaryStoreMap<Long, String> storeMap = getPrimaryStoreMap(tableName);
        final SecondaryTreeMap<Datum, Long, String> secondaryMap = getSecondaryMap(storeMap, schema, columnPosition);

        return new IndexedDataMap(secondaryMap, schema[columnPosition].getColName());
    }

    public List<Long> getAllTupleIds(String tableName) {
        final PrimaryStoreMap<Long, String> storeMap = getPrimaryStoreMap(tableName);
        return new ArrayList<>(storeMap.keySet());
    }

    /**
     * For better efficiency use ->  addTuplesToTable(String tableName, List<String> tuples)
     */
    public void addTupleToTable(String tableName, String tuple) {
        final PrimaryStoreMap<Long, String> storeMap = getPrimaryStoreMap(tableName);
        storeMap.putValue(tuple);
    }

    public void addTuplesToTable(String tableName, List<String> tuples) {
        final PrimaryStoreMap<Long, String> storeMap = getPrimaryStoreMap(tableName);
        for (String tuple : tuples) {
            storeMap.putValue(tuple);
        }
    }

    /**
     * For better efficiency use ->  deleteTuplesFromTable(String tableName, List<Long> rowIds)
     */
    public void deleteTupleFromTable(String tableName, Long rowId) {
        final PrimaryStoreMap<Long, String> storeMap = getPrimaryStoreMap(tableName);
        storeMap.remove(rowId);
    }

    public void deleteTuplesFromTable(String tableName, List<Long> rowIds) {
        final PrimaryStoreMap<Long, String> storeMap = getPrimaryStoreMap(tableName);
        for (Long rowId : rowIds) {
            storeMap.remove(rowId);
        }
    }

    public static IndexService getInstance() {
        if (indexService == null) {
            throw new RuntimeException("Index service never instantiated!");
        }
        return indexService;
    }

    public static void instantiate(File indexDir) {
        if (indexService == null) {
            indexService = new IndexService(indexDir);
        }
    }
}