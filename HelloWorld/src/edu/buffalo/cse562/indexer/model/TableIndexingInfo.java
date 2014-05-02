package edu.buffalo.cse562.indexer.model;

import edu.buffalo.cse562.model.TableInfo;
import edu.buffalo.cse562.schema.SchemaUtils;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

import java.util.ArrayList;
import java.util.List;

public class TableIndexingInfo extends TableInfo {

    private List<Integer> indexPositions;

    public TableIndexingInfo(String tableName, List<ColumnDefinition> columnDefinitions, Long size) {
        super(tableName, columnDefinitions, size);
        columnIndexesUsed = new ArrayList<>();
        for (int i = 0; i < columnDefinitions.size(); i++) {
            columnIndexesUsed.add(i);
        }

        indexPositions = new ArrayList<>();
    }

    public void addIndex(Column column) {
        final String columnName = column.getColumnName();
        final Integer oldPosition = SchemaUtils.getColumnIndexIn(columnDefinitions, columnName);
        indexPositions.add(oldPosition);
    }

    public List<Integer> getIndexPositions() {
        return indexPositions;
    }

}
