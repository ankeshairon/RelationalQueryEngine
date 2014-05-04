package edu.buffalo.cse562.schema;

import edu.buffalo.cse562.data.Datum;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

public class ColumnSchema {
    private String colName;
    private Datum.type type;
    private String tableName;
    private String tableAlias;
    private String columnAlias;
    private Expression expr;
    private String fullQualifiedName;
    private Boolean isDistinct;

    public ColumnSchema(String colName, String type) {
        this.colName = colName.toLowerCase();
        fullQualifiedName = this.colName;
        switch (type) {
            case "int":
            case "INT":
                this.type = Datum.type.LONG;
                break;
            case "float":
            case "FLOAT":
            case "decimal":
            case "DECIMAL":
                this.type = Datum.type.DOUBLE;
                break;
            case "date":
            case "DATE":
                this.type = Datum.type.DATE;
                break;
            case "boolean":
            case "BOOLEAN":
                this.type = Datum.type.BOOL;
                break;
            case "char":
            case "CHAR":
            case "varchar":
            case "VARCHAR":
            case "string":
            case "STRING":
                this.type = Datum.type.STRING;
                break;
            default:
                throw new UnsupportedOperationException(type + " type not supported!");
        }
        isDistinct = false;
    }

    public ColumnSchema(String colName, Datum.type type) {
        this.colName = colName.toLowerCase();
        this.type = type;
    }

    public String getColName() {
        return colName;
    }

    public String getFullQualifiedName() {
        return fullQualifiedName;
    }

    public boolean matchColumn(Column column) {
        String columnName = column.getColumnName();
        String tableName = column.getTable().getName();
        if (tableName == null) {
            return matchColumnNameOnly(columnName);
        } else {
            return matchTableAndColumnName(tableName, columnName);
        }
    }

    public boolean matchColumn(String col) {
        if (col.contains(".")) {
            String[] tokens = col.split("\\.");
            return matchTableAndColumnName(tokens[0], tokens[1]);
        } else {
            return matchColumnNameOnly(col);
        }
    }

    private boolean matchTableAndColumnName(String tableName, String columnName) {
        return matchColumnNameOnly(columnName) && matchTableName(tableName);
    }

    private boolean matchTableName(String tableName) {
        return tableName.equalsIgnoreCase(this.tableName) || tableName.equalsIgnoreCase(tableAlias);
    }

    public boolean matchColumnNameOnly(String columnName) {
        return columnName.equalsIgnoreCase(colName) || columnName.equals(columnAlias);
    }

    public boolean matchFullQualifiedName(String columnName) {
        return columnName.equalsIgnoreCase(fullQualifiedName);
    }

    public Datum.type getType() {
        return type;
    }

    public String getTblName() {
        return tableName;
    }

    public String getColumnAlias() {
        return columnAlias;
    }

    public Expression getExpression() {
        return expr;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public void setType(Datum.type type) {
        this.type = type;
    }

    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
        this.fullQualifiedName = tableName + "." + colName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
        this.fullQualifiedName = tableName + "." + colName;
    }

    public void setColumnAlias(String columnAlias) {
        this.columnAlias = columnAlias;
    }

    public void setExpression(Expression expr) {
        this.expr = expr;
    }

    public Boolean isDistinct() {
        return isDistinct;
    }

    public void setIsDistinct(Boolean isDistinct) {
        this.isDistinct = isDistinct;
    }

    @Override
    public String toString() {
        return "TableName=" + tableName + "   TableAlias=" + tableAlias + "   ColumnName=" + colName + "   ColumnAlias=" + columnAlias;
    }
}
