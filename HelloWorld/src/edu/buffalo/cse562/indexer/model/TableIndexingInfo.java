package edu.buffalo.cse562.indexer.model;

import edu.buffalo.cse562.model.TableInfo;
import edu.buffalo.cse562.schema.SchemaUtils;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableIndexingInfo extends TableInfo {

   /* //composite indexing
    private List<Integer> primaryIndexesOldPositions;
    private List<Integer> primaryIndexesNewPositions;
    private String primaryIndexName;

    private String primaryCumSecondaryIndexName;

    //separate
    private Integer secondaryIndexOldPosition;
    private String secondaryIndexName;*/

    //name-oldPosition
    private Map<String, Integer> indexes;

    public TableIndexingInfo(String tableName, List<ColumnDefinition> columnDefinitions, Long size) {
        super(tableName, columnDefinitions, size);
//        primaryIndexesOldPositions = new ArrayList<>();
//        primaryIndexesNewPositions = new ArrayList<>();
        columnIndexesUsed = new ArrayList<>();
        for (int i = 0; i < columnDefinitions.size(); i++) {
            columnIndexesUsed.add(i);
        }

        indexes = new HashMap<>();
    }

    public void addIndex(Column column) {
        final String columnName = column.getColumnName();
        final Integer oldPosition = SchemaUtils.getColumnIndexIn(columnDefinitions, columnName);
        indexes.put(columnName, oldPosition);
    }

    public Map<String, Integer> getIndexes() {
        return indexes;
    }


/*

    public void addIndex(Index index) {
        final List<String> indexedColumnsNames = index.getColumnsNames();
        Integer oldSchemaPosition;

        if (IndexingConstants.PRIMARY_KEY.equals(index.getType())) {
            if (indexedColumnsNames.size() == 1) {
                //simple primary index
                primaryIndexName = indexedColumnsNames.get(0);
                oldSchemaPosition = getColumnIndexIn(columnDefinitions, primaryIndexName);
                primaryIndexesOldPositions.add(oldSchemaPosition);
                primaryIndexesNewPositions.add(0);
            } else {
                //composite primary index
                primaryIndexName = tableName + DEFAULT_PRIMARY_INDEX_NAME;
                for (int i1 = 0; i1 < indexedColumnsNames.size(); i1++) {
                    String indexedColumnName = indexedColumnsNames.get(i1);
                    oldSchemaPosition = getColumnIndexIn(columnDefinitions, indexedColumnName);
                    primaryIndexesOldPositions.add(oldSchemaPosition);
                    primaryIndexesNewPositions.add(i1);
                }
                primaryCumSecondaryIndexName = indexedColumnsNames.get(0);
            }
        } else if (IndexingConstants.INDEX_KEY.equals(index.getType())) {
            secondaryIndexName = indexedColumnsNames.get(0);
            oldSchemaPosition = getColumnIndexIn(columnDefinitions, secondaryIndexName);
            secondaryIndexOldPosition = oldSchemaPosition;
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

    public String getPrimaryCumSecondaryIndexName() {
        return primaryCumSecondaryIndexName;
    }
*/
}
