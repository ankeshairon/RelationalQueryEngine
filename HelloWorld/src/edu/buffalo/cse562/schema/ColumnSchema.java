package edu.buffalo.cse562.schema;

import edu.buffalo.cse562.data.Datum;

public class ColumnSchema {
    public String colName;
    public Datum.type type;
    public String tblName;
    public String alias;

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

}
