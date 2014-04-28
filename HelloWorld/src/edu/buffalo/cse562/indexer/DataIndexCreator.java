package edu.buffalo.cse562.indexer;

import edu.buffalo.cse562.comparator.SerializableTupleComparator;
import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.indexer.model.TableIndexingInfo;
import edu.buffalo.cse562.indexer.serializer.DatumArraySerializer;
import edu.buffalo.cse562.indexer.serializer.DatumSerializer;
import edu.buffalo.cse562.operator.ScanOperator;
import edu.buffalo.cse562.schema.ColumnSchema;
import jdbm.PrimaryTreeMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.SecondaryKeyExtractor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static edu.buffalo.cse562.indexer.IndexingConstants.INDEX_DATABASE_NAME;

public class DataIndexCreator {
    private final RecordManager recordManager;
    private final File dataDir;

    public DataIndexCreator(File dataDir, File indexDir) throws IOException {
        recordManager = RecordManagerFactory.createRecordManager(indexDir.getAbsolutePath() + "//" + INDEX_DATABASE_NAME);
        this.dataDir = dataDir;
    }

    public void buildIndexes(TableIndexingInfo tableIndexingInfo) {
        final List<Integer> primaryIndexesColumnNewPositions = tableIndexingInfo.getPrimaryIndexesNewPositions();
        final ScanOperator scanOperator = new ScanOperator(dataDir, tableIndexingInfo, null);
        final ColumnSchema[] schema = scanOperator.getSchema();
        int commitCounter = 0;
        Datum[] tuple;

        final String primaryRecordName = tableIndexingInfo.getPrimaryIndexName();
        PrimaryTreeMap<Datum[], Datum[]> primaryTreeMap =
                recordManager.treeMap(tableIndexingInfo.getTableName() + "." + primaryRecordName,
                        new SerializableTupleComparator(primaryIndexesColumnNewPositions),
                        new DatumArraySerializer(getSchemaForSerializer(schema, tableIndexingInfo.getColumnIndexesUsed())),
                        new DatumArraySerializer(getSchemaForSerializer(schema, tableIndexingInfo.getPrimaryIndexesOldPositions())));

        registerForSecondaryIndexes(tableIndexingInfo, primaryIndexesColumnNewPositions, primaryTreeMap, schema);

        while ((tuple = scanOperator.readOneTuple()) != null) {
            primaryTreeMap.put(getPrimaryKey(tuple, primaryIndexesColumnNewPositions), tuple);
            if (++commitCounter == 1000) {
                commit();
                commitCounter = 0;
            }
        }
        commit();

        primaryTreeMap.entrySet();
//        if (secondaryIndexColumnPosition != null) {
//            secondaryTreeMap.entrySet();
//        }
    }

    private ColumnSchema[] getSchemaForSerializer(ColumnSchema[] superSchema, List<Integer> columnIndexesUsed) {
        int newLength = columnIndexesUsed.size();
        ColumnSchema[] schema = new ColumnSchema[newLength];

        for (int i = 0; i < newLength; i++) {
            schema[i] = superSchema[columnIndexesUsed.get(i)];
        }
        return schema;
    }

    private void registerForSecondaryIndexes(TableIndexingInfo tableIndexingInfo,
                                             List<Integer> primaryIndexesColumnPositions,
                                             PrimaryTreeMap<Datum[], Datum[]> primaryTreeMap,
                                             ColumnSchema[] schema) {
        createSecondaryIndexIfAny(primaryTreeMap,
                tableIndexingInfo.getSecondaryIndexOldPosition(),
                tableIndexingInfo.getSecondaryIndexName(),
                tableIndexingInfo.getTableName(),
                schema[tableIndexingInfo.getSecondaryIndexOldPosition()]);

        if (primaryIndexesColumnPositions.size() > 1) {
            createSecondaryIndexIfAny(primaryTreeMap,
                    tableIndexingInfo.getPrimaryIndexesOldPositions().get(0),
                    tableIndexingInfo.getPrimaryCumSecondaryIndexName(),
                    tableIndexingInfo.getTableName(),
                    schema[tableIndexingInfo.getPrimaryIndexesOldPositions().get(0)]);
        }
    }

    private void createSecondaryIndexIfAny(PrimaryTreeMap<Datum[], Datum[]> primaryTreeMap,
                                           Integer secondaryIndexOldPosition,
                                           String secondaryIndexName,
                                           String tableName,
                                           ColumnSchema secondaryKeyColumnSchema) {
        if (secondaryIndexOldPosition != null) {
            primaryTreeMap.secondaryTreeMap(tableName + "." + secondaryIndexName,
                    getSecondaryKeyExtractor(secondaryIndexOldPosition),
                    new DatumSerializer(secondaryKeyColumnSchema));
            primaryTreeMap.clear();
        }
    }

    private Datum[] getPrimaryKey(Datum[] tuple, List<Integer> primaryIndexPositions) {
        Datum[] primaryKey = new Datum[primaryIndexPositions.size()];

        for (int i = 0; i < primaryIndexPositions.size(); i++) {
            primaryKey[i] = tuple[primaryIndexPositions.get(i)];
        }
        return primaryKey;
    }

    private void commit() {
        try {
            recordManager.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SecondaryKeyExtractor<Datum, Datum[], Datum[]> getSecondaryKeyExtractor(final Integer secondaryIndexOldPosition) {
        return new SecondaryKeyExtractor<Datum, Datum[], Datum[]>() {
            @Override
            public Datum extractSecondaryKey(Datum[] primaryKey, Datum[] tuple) {
                return tuple[secondaryIndexOldPosition];
            }
        };
    }

    public void finish() {
        try {
            recordManager.close();
        } catch (IOException e) {
            System.out.println("Error closing record manager!");
            e.printStackTrace();
        }
    }
}
