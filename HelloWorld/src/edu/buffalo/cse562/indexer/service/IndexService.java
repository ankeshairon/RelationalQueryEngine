package edu.buffalo.cse562.indexer.service;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.indexer.modifier.Indexer;
import edu.buffalo.cse562.schema.ColumnSchema;
import jdbm.PrimaryStoreMap;
import jdbm.RecordManager;
import jdbm.SecondaryTreeMap;

import java.io.File;
import java.util.Set;

public class IndexService extends Indexer {

    private static IndexService indexService;

    private RecordManager recordManager;

    private IndexService(File indexDir) {
        recordManager = getRecordManager(indexDir);
    }

    /**
     * Input param - tableName, input schema, position of indexed column in schema
     * returns Map<Datum, List<<String>> where Datum is the key on which it is indexed & List<String> is the list of raw tuples
     */
    public IndexedDataMap getTuplesOfIndexesAsPer(String tableName, ColumnSchema[] schema, Integer columnPosition) {

        final PrimaryStoreMap<Long, String> storeMap = getPrimaryStoreMap(recordManager, tableName);
        final SecondaryTreeMap<Datum, Long, String> secondaryMap = getSecondaryMap(storeMap, schema, columnPosition);

        return new IndexedDataMap() {
            @Override
            public Set<Datum> keySet() {
                return secondaryMap.keySet();
            }

            @Override
            public Iterable<String> get(Datum key) {
                return secondaryMap.getPrimaryValues(key);
            }
        };
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
        } else {
            throw new RuntimeException("Index service already instantiated!");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close(recordManager);
    }
}