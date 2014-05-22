package edu.buffalo.cse562.indexer.modifier;

import edu.buffalo.cse562.indexer.model.TableIndexingInfo;
import edu.buffalo.cse562.operator.FileScanner;
import jdbm.PrimaryStoreMap;

import java.io.File;
import java.util.Collection;

public class IndexCreator extends Indexer/* implements Runnable*/ {
    private final File dataDir;
    private Collection<TableIndexingInfo> tableIndexingInfos;

    public IndexCreator(File dataDir, File indexDir, Collection<TableIndexingInfo> tableIndexingInfos) {
        super(indexDir);
        this.dataDir = dataDir;
        this.tableIndexingInfos = tableIndexingInfos;
    }

    //    @Override
    public void run() {
        PrimaryStoreMap<Long, String> storeMap;
        FileScanner fileScanner;
        String tableName;
        String line;

        for (TableIndexingInfo tableIndexingInfo : tableIndexingInfos) {

            fileScanner = new FileScanner(dataDir, tableIndexingInfo);
            tableName = tableIndexingInfo.getTableName();
            storeMap = getPrimaryStoreMap(tableName);
            registerSecondaryIndexes(storeMap, fileScanner.getSchema(), tableIndexingInfo.getIndexPositions(), tableName);

//            int counter = 0;
            while ((line = fileScanner.readOneLine()) != null) {
                storeMap.putValue(line);
//                if (++counter == 1000) {
//                    commit(recordManager);
//                    counter = 0;
//                }
            }
            commit();
        }
        close();
    }
}
