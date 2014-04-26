package edu.buffalo.cse562.indexer;

import edu.buffalo.cse562.comparator.SerializableTupleComparator;
import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.indexer.model.TableIndexingInfo;
import edu.buffalo.cse562.indexer.serializer.DatumArraySerializer;
import edu.buffalo.cse562.indexer.serializer.DatumSerializer;
import edu.buffalo.cse562.operator.ScanOperator;
import jdbm.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static edu.buffalo.cse562.indexer.IndexingConstants.INDEX_DATABASE_NAME;

public class DataIndexer {
    private final RecordManager recordManager;
    private final File dataDir;

    public DataIndexer(File dataDir, File indexDir) throws IOException {
        recordManager = RecordManagerFactory.createRecordManager(indexDir.getAbsolutePath() + "//" + INDEX_DATABASE_NAME);
        this.dataDir = dataDir;
    }

    public void buildIndexes(TableIndexingInfo tableIndexingInfo) {
        final List<Integer> primaryIndexesColumnPositions = tableIndexingInfo.getPrimaryIndexesNewPositions();
        final String primaryRecordName = tableIndexingInfo.getPrimaryIndexName();

        PrimaryTreeMap<Datum[], Datum[]> primaryTreeMap = recordManager.treeMap(tableIndexingInfo.getTableName() + "." + primaryRecordName, new SerializableTupleComparator(primaryIndexesColumnPositions), DatumArraySerializer.INSTANCE, DatumArraySerializer.INSTANCE);

        createSecondaryIndexIfAny(primaryTreeMap,
                                                    tableIndexingInfo.getSecondaryIndexNewPosition(),
                                                    tableIndexingInfo.getSecondaryIndexName(),
                                                    tableIndexingInfo.getTableName());

        if(primaryIndexesColumnPositions.size() > 1){
            createSecondaryIndexIfAny(primaryTreeMap,
                                                        tableIndexingInfo.getPrimaryIndexesNewPositions().get(0),
                                                        tableIndexingInfo.getPrimaryCumSecondaryIndexName(),
                                                        tableIndexingInfo.getTableName());
        }

        ScanOperator scanOperator = new ScanOperator(dataDir, tableIndexingInfo, null);
        int commitCounter = 0;
        Datum[] tuple;

        while ((tuple = scanOperator.readOneTuple()) != null) {
            primaryTreeMap.put(getPrimaryKey(tuple, primaryIndexesColumnPositions), tuple);
            if (++commitCounter == 50000) {
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

    private void createSecondaryIndexIfAny(PrimaryTreeMap<Datum[], Datum[]> primaryTreeMap,
                                                                    Integer secondaryIndexNewPosition,
                                                                    String secondaryIndexName,
                                                                    String tableName) {
        if (secondaryIndexNewPosition != null) {
            primaryTreeMap.secondaryTreeMap(tableName + "." + secondaryIndexName, getSecondaryKeyExtractor(secondaryIndexNewPosition), DatumSerializer.INSTANCE);
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

    private SecondaryKeyExtractor<Datum, Datum[], Datum[]> getSecondaryKeyExtractor(final Integer secondaryIndexColumnPosition) {
        return new SecondaryKeyExtractor<Datum, Datum[], Datum[]>() {
            @Override
            public Datum extractSecondaryKey(Datum[] key, Datum[] tuple) {
                return tuple[secondaryIndexColumnPosition];
            }
        };
    }

    public void cleanUp() {
        try {
            recordManager.close();
        } catch (IOException e) {
            System.out.println("Error closing record manager!");
            e.printStackTrace();
        }
    }
}
