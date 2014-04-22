package edu.buffalo.cse562.indexer.model;

import java.util.HashMap;
import java.util.Map;

import static edu.buffalo.cse562.indexer.model.KeyConstants.INDEX_KEY;
import static edu.buffalo.cse562.indexer.model.KeyConstants.PRIMARY_KEY;

public class TableIndexingInfo {
    //unique composition
    //columnName - schema position
    private Map<String, Integer> primaryIndexedColumns;

    //individually unique
    //columnName - schema position
    private Map<String, Integer> secondaryIndexedColumns;


    public TableIndexingInfo() {
        primaryIndexedColumns = new HashMap<>();
        secondaryIndexedColumns = new HashMap<>();
    }


    public void addIndexColumn(String indexType, String columnName, Integer schemaPosition) {
        if (PRIMARY_KEY.equals(indexType)) {
            primaryIndexedColumns.put(columnName, schemaPosition);
        } else if (INDEX_KEY.equals(indexType)) {
            secondaryIndexedColumns.put(columnName, schemaPosition);
        } else {
            throw new UnsupportedOperationException("Woaaa! Some new index type encountered!");
        }
    }
}
