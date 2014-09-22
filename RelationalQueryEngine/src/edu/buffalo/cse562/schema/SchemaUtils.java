package edu.buffalo.cse562.schema;

import edu.buffalo.cse562.model.TableInfo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

import java.util.List;

public class SchemaUtils {

    public static Integer getColumnIndexInColDefn(List<ColumnDefinition> schema, String columnName) {
        for (int i = 0; i < schema.size(); i++) {
            if (columnName.equalsIgnoreCase(schema.get(i).getColumnName())) {
                return i;
            }
        }
        return -1;
//        throw new ArrayIndexOutOfBoundsException("Column Name " + columnName + " not found in schema");
    }

    public static Integer getColumnIndexInSchema(ColumnSchema[] schema, String columnName) {
        for (int i = 0; i < schema.length; i++) {
            if (columnName.equalsIgnoreCase(schema[i].getColName())) {
                return i;
            }
        }
        throw new ArrayIndexOutOfBoundsException("Column Name " + columnName + " not found in schema");

    }

    public static int getColIndexInSchema(Column columnInCondition, ColumnSchema[] schema) {
        for (int i = 0; i < schema.length; i++) {
            if (schema[i].matchColumn(columnInCondition)) {
                return i;
            }
        }
        return -1;
    }

    public static ColumnSchema[] createSchemaFromTableInfo(TableInfo tableInfo) {
        final List<ColumnDefinition> allColumnDefinitions = tableInfo.getColumnDefinitions();
        ColumnSchema[] schema = new ColumnSchema[allColumnDefinitions.size()];
        ColumnDefinition columnDefinition;

        for (int i = 0; i < allColumnDefinitions.size(); i++) {
            columnDefinition = allColumnDefinitions.get(i);
            schema[i] = new ColumnSchema(columnDefinition.getColumnName(), columnDefinition.getColDataType().getDataType());
            schema[i].setTableName(tableInfo.getTableName());
            schema[i].setTableAlias(tableInfo.getAlias());
        }
        return schema;
    }
}
