package edu.buffalo.cse562.schema;

import edu.buffalo.cse562.data.Datum;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

public class ColumnSchema {
    private String colName;
    private Datum.type type;
    private String tblName;
    private String alias;
    private Expression expr;

    public ColumnSchema(String colName, String type) {
        this.colName = colName.toLowerCase();
        if (type.equalsIgnoreCase("int")) {
            this.type = Datum.type.LONG;
        } else if (type.equalsIgnoreCase("float") || type.equalsIgnoreCase("decimal")) {
            this.type = Datum.type.FLOAT;
        } else if (type.equalsIgnoreCase("date")) {
            this.type = Datum.type.DATE;
        } else if (type.equalsIgnoreCase("boolean")) {
            this.type = Datum.type.BOOL;
        } else if (type.equalsIgnoreCase("char") || type.equalsIgnoreCase("varchar") || type.equalsIgnoreCase("string")) {
            this.type = Datum.type.STRING;
        }
    }

    public ColumnSchema(String colName, Datum.type type) {
        this.colName = colName.toLowerCase();
        this.type = type;
    }

    public String getColName() {
        return colName;
    }

    public boolean matchColumn(Column column) {
        String columnName = column.getColumnName();
        String tableName = column.getTable().getName();
        if (tableName == null) {
            return matchColumnNameOnly(columnName);
        } else
            return matchTableAndColumnName(tableName, columnName);
    }

    public boolean matchColumn(String col) {
        if (col.contains("\\.")) {
            String[] tokens = col.split("\\.");
            return matchTableAndColumnName(tokens[0], tokens[1]);
        } else {
            return matchColumnNameOnly(col);
        }
    }

    private boolean matchTableAndColumnName(String tableName, String columnName) {
        return matchColumnNameOnly(columnName) && tableName.equalsIgnoreCase(this.getTblName());
    }

    private boolean matchColumnNameOnly(String columnName) {
        return columnName.equalsIgnoreCase(colName);
    }

    public Datum.type getType() {
        return type;
    }

    public String getTblName() {
        return tblName;
    }

    public String getAlias() {
        return alias;
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

    public void setTblName(String tblName) {
        this.tblName = tblName;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
    
    public void setExpression(Expression expr) {
    	this.expr = expr;
    }
}
