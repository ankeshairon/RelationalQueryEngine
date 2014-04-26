package edu.buffalo.cse562.model;

import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

import java.util.List;

public class TableInfo {
    protected String tableName;
    private String alias;
    private final Long size;
    protected final List<ColumnDefinition> columnDefinitions;
    protected List<Integer> columnIndexesUsed;

    public TableInfo(String tableName, List<ColumnDefinition> columnDefinitions, Long size) {
        this.tableName = tableName.toLowerCase();
        this.columnDefinitions = columnDefinitions;
        this.size = size;
    }

    public List<ColumnDefinition> getColumnDefinitions() {
        return columnDefinitions;
    }

    public String getTableName() {
        return tableName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Long getSize() {
        return size;
    }

    public List<Integer> getColumnIndexesUsed() {
        return columnIndexesUsed;
    }

    public void setColumnIndexesUsed(List<Integer> columnIndexesUsed) {
        this.columnIndexesUsed = columnIndexesUsed;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TableInfo)) {
            return false;
        }

        TableInfo that = (TableInfo) o;
        if (!this.tableName.equals(that.tableName)) {
            return false;
        }

        return (alias != null) && alias.equals(that.alias);
    }
}

