package edu.buffalo.cse562.indexer.modifier;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.indexer.serializer.DatumSerializer;
import edu.buffalo.cse562.schema.ColumnSchema;
import jdbm.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static edu.buffalo.cse562.data.DatumUtils.getDatumOfTypeFromValue;
import static edu.buffalo.cse562.indexer.constants.IndexingConstants.RECORD_MANAGER_NAME;

public abstract class Indexer {

    private final RecordManager recordManager;
    private Map<String, PrimaryStoreMap<Long, String>> primaryStoreMaps;

    protected Indexer(File indexDir) {
        try {
            recordManager = RecordManagerFactory.createRecordManager(indexDir.getAbsolutePath() + "//" + RECORD_MANAGER_NAME);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating record manager!");
        }
        primaryStoreMaps = new HashMap<>();
    }

    public PrimaryStoreMap<Long, String> getPrimaryStoreMap(String tableName) {
        if (primaryStoreMaps.containsKey(tableName)) {
            return primaryStoreMaps.get(tableName);
        } else {
            final PrimaryStoreMap<Long, String> map = recordManager.storeMap(tableName);
            primaryStoreMaps.put(tableName, map);
            return map;
        }
    }

    protected SecondaryTreeMap<Datum, Long, String> getSecondaryMap(PrimaryStoreMap<Long, String> storeMap,
                                                                    ColumnSchema[] schema,
                                                                    Integer position, String tableName) {
        ColumnSchema columnSchema = schema[position];
        return storeMap.secondaryTreeMap(
                tableName + columnSchema.getColName(),
                getSecondaryKeyExtractor(position, columnSchema),
                new DatumSerializer(columnSchema)
        );
    }

    private SecondaryKeyExtractor<Datum, Long, String> getSecondaryKeyExtractor(final Integer position, final ColumnSchema columnSchema) {
        return new SecondaryKeyExtractor<Datum, Long, String>() {
            @Override
            public Datum extractSecondaryKey(Long primaryKey, String tuple) {
                final String cellValue = (tuple.split("\\|"))[position];
                return getDatumOfTypeFromValue(columnSchema.getType(), cellValue);
            }
        };
    }

    public void registerSecondaryIndexes(PrimaryStoreMap<Long, String> storeMap, ColumnSchema[] schema, Collection<Integer> indexPositions, String tableName) {
        final Iterator<Integer> iterator = indexPositions.iterator();
        Integer position;

        while (iterator.hasNext()) {
            position = iterator.next();
            getSecondaryMap(storeMap, schema, position, tableName);
        }
    }

    public void commit() {
        try {
            recordManager.commit();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error closing record manager!");
        }
    }

    public void close() {
        try {
            recordManager.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error closing record manager!");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
