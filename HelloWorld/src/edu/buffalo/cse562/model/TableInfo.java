package edu.buffalo.cse562.model;

import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

import java.util.List;

public class TableInfo {
    private String name;
    private String alias;
    private final Long size;
    private final List<ColumnDefinition> columnDefinitions;
    private List<Integer> columnIndexesUsed;

    public TableInfo(String name, List<ColumnDefinition> columnDefinitions, Long size) {
        this.name = name;
        this.columnDefinitions = columnDefinitions;
        this.size = size;
    }

    public List<ColumnDefinition> getColumnDefinitions() {
        return columnDefinitions;
    }

    public String getName() {
        return name;
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
        if (!this.name.equals(that.name)) {
            return false;
        }

        if (this.alias == that.alias || (alias != null && alias.equals(that.alias))) {
            return true;
        }
        return false;
    }
}

