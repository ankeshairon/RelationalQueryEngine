package edu.buffalo.cse562.indexer.modifier;

import edu.buffalo.cse562.indexer.model.TableIndexingInfo;
import edu.buffalo.cse562.operator.FileScanner;
import edu.buffalo.cse562.schema.ColumnSchema;
import jdbm.PrimaryStoreMap;
import jdbm.RecordManager;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

public class IndexCreator extends Indexer implements Runnable {
    private Collection<TableIndexingInfo> tableIndexingInfos;
    private final File dataDir;
    private File indexDir;

    public IndexCreator(File dataDir, File indexDir, Collection<TableIndexingInfo> tableIndexingInfos) {
        this.indexDir = indexDir;
        this.dataDir = dataDir;
        this.tableIndexingInfos = tableIndexingInfos;
    }

    @Override
    public void run() {
        RecordManager recordManager = getRecordManager(indexDir);
        PrimaryStoreMap<Long, String> storeMap;
        FileScanner fileScanner;
        String line;

        for (TableIndexingInfo tableIndexingInfo : tableIndexingInfos) {

            fileScanner = new FileScanner(dataDir, tableIndexingInfo);
            storeMap = getPrimaryStoreMap(recordManager, tableIndexingInfo.getTableName());
            registerIndexes(storeMap, fileScanner.getSchema(), tableIndexingInfo);

//            int counter = 0;
            while ((line = fileScanner.readOneLine()) != null) {
                storeMap.putValue(line);
//                if (++counter == 1000) {
//                    commit(recordManager);
//                    counter = 0;
//                }
            }
            commit(recordManager);
        }
        close(recordManager);
    }

    private void registerIndexes(PrimaryStoreMap<Long, String> storeMap, ColumnSchema[] schema, TableIndexingInfo tableIndexingInfo) {
        final Iterator<Integer> iterator = tableIndexingInfo.getIndexPositions().iterator();
        Integer position;

        while (iterator.hasNext()) {
            position = iterator.next();
            getSecondaryMap(storeMap, schema, position);
        }
    }

}
