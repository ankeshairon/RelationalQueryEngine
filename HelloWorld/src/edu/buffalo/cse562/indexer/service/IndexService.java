package edu.buffalo.cse562.indexer.service;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.indexer.modifier.Indexer;
import edu.buffalo.cse562.model.TableInfo;
import edu.buffalo.cse562.schema.ColumnSchema;
import jdbm.PrimaryStoreMap;
import jdbm.SecondaryTreeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static edu.buffalo.cse562.schema.SchemaUtils.createSchemaFromTableInfo;

/**
 * This class interacts directly with PrimaryStoreMap & has methods not requiring any column names (SecondaryIndexes)
 */
public class IndexService extends Indexer {

    private static IndexService indexService;

    private IndexService(File indexDir) {
        super(indexDir);
    }

    //use this to get an object.. This has been instantiated once at the beginning of execution & will always give a valid object inside the program
    public static IndexService getInstance() {
        if (indexService == null) {
            throw new RuntimeException("Index service never instantiated!");
        }
        return indexService;
    }

    public static IndexService instantiate(File indexDir) {
        if (indexService == null) {
            indexService = new IndexService(indexDir);
        }
        return indexService;
    }

    /**
     * Input param - tableName, input schema, position of indexed column in schema
     * returns Map<Datum, List<<String>> where Datum is the key on which it is indexed & List<String> is the list of raw tuples
     */
    public IndexedDataMap getIndexedDataFor(String tableName, ColumnSchema[] schema, Integer columnPosition) {

        final PrimaryStoreMap<Long, String> storeMap = getPrimaryStoreMap(tableName);
        final SecondaryTreeMap<Datum, Long, String> secondaryMap = getSecondaryMap(storeMap, schema, columnPosition, tableName);

        return new IndexedDataMap(secondaryMap, schema[columnPosition].getColName());
    }

    public List<Long> getAllTupleIds(String tableName) {
        final PrimaryStoreMap<Long, String> storeMap = getPrimaryStoreMap(tableName);
        return new ArrayList<>(storeMap.keySet());
    }

    /**
     * For better efficiency use ->  addTuplesToTable(String tableName, List<String> tuples)
     */
    //NOT TO BE USED.. requires extracting secondary index maps for every tuple
   /* public void addTupleToTable(String tableName, String tuple) {
        final PrimaryStoreMap<Long, String> storeMap = getPrimaryStoreMap(tableName.toLowerCase());
        storeMap.putValue(tuple);
    }*/
    public void addTuplesToTable(TableInfo tableInfo, List<String> tuples) {
        final PrimaryStoreMap<Long, String> storeMap = registerSecondaryMapsAndGetPrimaryMap(tableInfo);

        for (String tuple : tuples) {
            storeMap.putValue(tuple);
        }
    }

    /**
     * For better efficiency use ->  deleteTuplesFromTable(String tableName, List<Long> rowIds)
     */
    //NOT TO BE USED.. requires extracting secondary index maps for every tuple
    /*public void deleteTupleFromTable(String tableName, Long rowId) {
        final PrimaryStoreMap<Long, String> storeMap = getPrimaryStoreMap(tableName);
        storeMap.remove(rowId);
    }*/
    public void deleteTuplesFromTable(TableInfo tableInfo, List<Long> rowIds) {
        final PrimaryStoreMap<Long, String> storeMap = registerSecondaryMapsAndGetPrimaryMap(tableInfo);

        for (Long rowId : rowIds) {
            storeMap.remove(rowId);
        }
    }

    public void updateTuples(TableInfo tableInfo, Map<Long, String> updatedTuples) {
        final PrimaryStoreMap<Long, String> storeMap = registerSecondaryMapsAndGetPrimaryMap(tableInfo);

        for (Map.Entry<Long, String> updatedTuple : updatedTuples.entrySet()) {
            storeMap.put(updatedTuple.getKey(), updatedTuple.getValue());
        }

    }

    private PrimaryStoreMap<Long, String> registerSecondaryMapsAndGetPrimaryMap(TableInfo tableInfo) {
        final PrimaryStoreMap<Long, String> storeMap = getPrimaryStoreMap(tableInfo.getTableName());
        ColumnSchema[] schema = createSchemaFromTableInfo(tableInfo);

        registerSecondaryIndexes(storeMap, schema, tableInfo.getIndexesForAllColumnDefinitions(), tableInfo.getTableName());
        return storeMap;
    }

    @Override
    public void close() {
        super.close();
        indexService = null;
    }
}