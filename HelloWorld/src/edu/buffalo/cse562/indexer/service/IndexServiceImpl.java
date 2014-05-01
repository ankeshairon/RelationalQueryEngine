package edu.buffalo.cse562.indexer.service;

import edu.buffalo.cse562.data.Datum;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class IndexServiceImpl implements IndexService {

    private final File indexDir;

    public IndexServiceImpl(File indexDir) {
        this.indexDir = indexDir;
    }

    //todo friggin implement it!
    @Override
    public Map<Datum, List<String>> getTuplesOfIndexesAsPer(String tableName, String columnName) {
        RecordManager recordManager = null;
        try {
            recordManager = RecordManagerFactory.createRecordManager(indexDir.getAbsolutePath() + "//" + tableName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating record manager");
        }

//        recordManager.

        return null;
    }
}
