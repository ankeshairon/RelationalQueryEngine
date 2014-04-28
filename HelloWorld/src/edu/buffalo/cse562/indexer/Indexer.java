package edu.buffalo.cse562.indexer;

import edu.buffalo.cse562.indexer.model.TableIndexingInfo;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Indexer {

    private List<TableIndexingInfo> tableIndexingInfos;
    private DataIndexCreator dataIndexCreator;

    public Indexer(List<TableIndexingInfo> tableIndexingInfos, File dataDir, File indexDir) throws IOException {
        this.tableIndexingInfos = tableIndexingInfos;
        dataIndexCreator = new DataIndexCreator(dataDir, indexDir);
    }

    public void createIndexes(){
        //todo parallelize the index creation process
        for (TableIndexingInfo tableIndexingInfo : tableIndexingInfos) {
            dataIndexCreator.buildIndexes(tableIndexingInfo);
        }
        dataIndexCreator.finish();
    }
}
