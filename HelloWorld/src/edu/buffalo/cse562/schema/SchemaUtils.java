package edu.buffalo.cse562.schema;

import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

import java.util.List;

public class SchemaUtils {

    public static Integer getColumnIndexInColDefn(List<ColumnDefinition> schema, String columnName) {
        for (int i = 0; i < schema.size(); i++) {
            if (columnName.equals(schema.get(i).getColumnName())) {
                return i;
            }
        }
        throw new ArrayIndexOutOfBoundsException("Column Name " + columnName + " not found in schema");
    }

    public static Integer getColumnIndexInColSchema(ColumnSchema[] schema, String columnName) {
        for (int i = 0; i < schema.length; i++) {
            if (columnName.equals(schema[i].getColName())) {
                return i;
            }
        }
        throw new ArrayIndexOutOfBoundsException("Column Name " + columnName + " not found in schema");

    }
}
