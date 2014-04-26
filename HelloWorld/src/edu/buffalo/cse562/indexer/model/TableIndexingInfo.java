package edu.buffalo.cse562.indexer.model;

import edu.buffalo.cse562.indexer.IndexingConstants;
import edu.buffalo.cse562.model.TableInfo;
import edu.buffalo.cse562.schema.SchemaUtils;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.Index;

import java.util.ArrayList;
import java.util.List;

import static edu.buffalo.cse562.indexer.IndexingConstants.DEFAULT_PRIMARY_INDEX_NAME;

public class TableIndexingInfo extends TableInfo {

    //composite indexing
    private List<Integer> primaryIndexesOldPositions;
    private List<Integer> primaryIndexesNewPositions;
    private String primaryIndexName;

    private String primaryCumSecondaryIndexName;

    //separate
    private Integer secondaryIndexOldPosition;
    private Integer secondaryIndexNewPosition;
    private String secondaryIndexName;


    public TableIndexingInfo(String tableName, List<ColumnDefinition> columnDefinitions, Long size) {
        super(tableName, columnDefinitions, size);
        primaryIndexesOldPositions = new ArrayList<>();
        primaryIndexesNewPositions = new ArrayList<>();
        columnIndexesUsed = new ArrayList<>();
        for (int i = 0; i < columnDefinitions.size(); i++) {
            columnIndexesUsed.add(i);
        }
    }


    public void addIndex(Index index, int i) {
        final List<String> indexedColumnsNames = index.getColumnsNames();
        Integer oldSchemaPosition;
        Integer newSchemaPosition = i;

        if (IndexingConstants.PRIMARY_KEY.equals(index.getType())) {
            if (indexedColumnsNames.size() == 1) {
                //simple primary index
                primaryIndexName = indexedColumnsNames.get(0);
                oldSchemaPosition = SchemaUtils.getColumnIndexIn(columnDefinitions, primaryIndexName);
                primaryIndexesOldPositions.add(oldSchemaPosition);
                primaryIndexesNewPositions.add(newSchemaPosition);
            } else {
                //composite primary index
                primaryIndexName = tableName + DEFAULT_PRIMARY_INDEX_NAME;
                for (String indexedColumnName : indexedColumnsNames) {
                    oldSchemaPosition = SchemaUtils.getColumnIndexIn(columnDefinitions, indexedColumnName);
                    primaryIndexesOldPositions.add(oldSchemaPosition);
                    primaryIndexesNewPositions.add(newSchemaPosition);
                }
                primaryCumSecondaryIndexName = indexedColumnsNames.get(0);
            }
        } else if (IndexingConstants.INDEX_KEY.equals(index.getType())) {
            secondaryIndexName = indexedColumnsNames.get(0);
            oldSchemaPosition = SchemaUtils.getColumnIndexIn(columnDefinitions, secondaryIndexName);
            secondaryIndexOldPosition = oldSchemaPosition;
            secondaryIndexNewPosition = newSchemaPosition;
        } else {
            throw new UnsupportedOperationException("Woaaa! Some new index type encountered!");
        }
    }

    public List<Integer> getPrimaryIndexesOldPositions() {
        return primaryIndexesOldPositions;
    }

    public String getPrimaryIndexName() {
        return primaryIndexName;
    }

    public Integer getSecondaryIndexOldPosition() {
        return secondaryIndexOldPosition;
    }

    public String getSecondaryIndexName() {
        return secondaryIndexName;
    }

    public List<Integer> getPrimaryIndexesNewPositions() {
        return primaryIndexesNewPositions;
    }

    public Integer getSecondaryIndexNewPosition() {
        return secondaryIndexNewPosition;
    }

    public String getPrimaryCumSecondaryIndexName() {
        return primaryCumSecondaryIndexName;
    }
}
