package edu.buffalo.cse562.indexer;

import edu.buffalo.cse562.indexer.model.TableIndexingInfo;
import edu.buffalo.cse562.indexer.modifier.IndexCreator;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class IndexBuilder {

    private Map<String, TableIndexingInfo> tableIndexingInfos;
    private final File indexDir;
    private final File dataDir;

    public IndexBuilder(Map<String, TableIndexingInfo> tableIndexingInfos, File dataDir, File indexDir) throws IOException {
        this.tableIndexingInfos = tableIndexingInfos;
        this.indexDir = indexDir;
        this.dataDir = dataDir;
    }

    public void createIndexes() {
        tempHackToRemoveAliases();
        final Collection<TableIndexingInfo> indexingInfos = tableIndexingInfos.values();

        new IndexCreator(dataDir, indexDir, indexingInfos).run();

//        ExecutorService executorService = Executors.newFixedThreadPool(indexingInfos.size());
//
//        for (TableIndexingInfo tableIndexingInfo : indexingInfos) {
//            executorService.execute(new IndexCreator(dataDir, indexDir, tableIndexingInfo));
//        }
//        executorService.shutdown();

//        try {
//            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
//        } catch (InterruptedException e) {
//            System.out.println("Index creation interrupted");
//            e.printStackTrace();
//        }
    }

    private void tempHackToRemoveAliases() {
        tableIndexingInfos.remove("n1");
        tableIndexingInfos.remove("n2");
    }
}
