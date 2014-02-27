package edu.buffalo.cse562.schema;

import edu.buffalo.cse562.data.Datum;

public class ColumnSchema {
    private String colName;
    private Datum.type type;
    private String tblName;
    private String alias;

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

    public Datum.type getType() {
        return type;
    }

    public String getTblName() {
        return tblName;
    }

    public String getAlias() {
        return alias;
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
}
