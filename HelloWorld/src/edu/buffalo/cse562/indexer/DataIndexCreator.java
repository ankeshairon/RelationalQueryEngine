package edu.buffalo.cse562.indexer;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.indexer.model.TableIndexingInfo;
import edu.buffalo.cse562.indexer.serializer.DatumSerializer;
import edu.buffalo.cse562.operator.FileScanner;
import edu.buffalo.cse562.schema.ColumnSchema;
import jdbm.PrimaryStoreMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.SecondaryKeyExtractor;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static edu.buffalo.cse562.data.DatumUtils.getDatumOfTypeFromValue;

public class DataIndexCreator implements Runnable {
    private TableIndexingInfo tableIndexingInfo;
    private final File dataDir;
    private File indexDir;

    public DataIndexCreator(File dataDir, File indexDir, TableIndexingInfo tableIndexingInfo) {
        this.indexDir = indexDir;
        this.dataDir = dataDir;
        this.tableIndexingInfo = tableIndexingInfo;
    }

    @Override
    public void run() {
        FileScanner fileScanner = new FileScanner(dataDir, tableIndexingInfo);
        RecordManager recordManager = null;
//        int commitCounter = 0;
        String line;

        try {
            recordManager = RecordManagerFactory.createRecordManager(indexDir.getAbsolutePath() + "//" + tableIndexingInfo.getTableName());
        } catch (IOException e) {
            System.out.println("Error creating record manager!");
            e.printStackTrace();
        }

        final PrimaryStoreMap<Long, String> storeMap = recordManager.storeMap("storeMap");
        registerIndexes(storeMap, fileScanner.getSchema());


        while ((line = fileScanner.readOneLine()) != null) {
            storeMap.putValue(line);
//            if (++commitCounter == 50000) {
//                commit(recordManager);
//                commitCounter = 0;
//            }
        }
        commit(recordManager);
        close(recordManager);
    }

    private void registerIndexes(PrimaryStoreMap<Long, String> storeMap, ColumnSchema[] schema) {
        //index name - position
        final Iterator<Map.Entry<String, Integer>> iterator = tableIndexingInfo.getIndexes().entrySet().iterator();
        Map.Entry<String, Integer> index;
        Integer position;

        while (iterator.hasNext()) {
            index = iterator.next();
            position = index.getValue();
            storeMap.secondaryTreeMap(
                    index.getKey(),
                    getSecondaryKeyExtractor(position, schema[position]),
                    new DatumSerializer(schema[position]));
        }
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

    private void commit(RecordManager recordManager) {
        try {
            recordManager.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close(RecordManager recordManager) {
        try {
            recordManager.close();
        } catch (IOException e) {
            System.out.println("Error closing record manager!");
            e.printStackTrace();
        }
    }
}
