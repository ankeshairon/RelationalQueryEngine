package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.data.*;
import edu.buffalo.cse562.model.TableInfo;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

import java.io.*;
import java.util.HashMap;
import java.util.List;

public class ScanOperator implements Operator {
    public ColumnSchema[] schema;

    private Long tableSize;
    private BufferedReader input;
    private String tableName;
    private HashMap<String, TableInfo> tables;
    private File dataDir;

    public ScanOperator(File dataDir, Table table, HashMap<String, TableInfo> tables, ColumnSchema[] finalSchema) {
        this.tables = tables;
        this.tableName = table.getName();
        this.dataDir = dataDir;
        this.schema = finalSchema;
        tableSize = tables.get(table.getName()).getSize();
        makeSchema(table);
        reset();
    }

    public void makeSchema(Table table) {
    	if (schema == null) {
    		List<ColumnDefinition> colDefns = tables.get(table.getName().toLowerCase()).getColumnDefinitions();
    		schema = new ColumnSchema[colDefns.size()];
    		int i = 0;
    		for (ColumnDefinition cd : colDefns) {
    			schema[i] = new ColumnSchema(cd.getColumnName(), cd.getColDataType().getDataType());
    			schema[i].setTableName(tableName);
                schema[i].setTableAlias(table.getAlias());
    			i++;
    		}
    	}
    }

    @Override
    public Datum[] readOneTuple() {
        if (input == null) {
            return null;
        }
        String line = null;
        try {
            line = input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (line == null) {
            return null;
        }
        String[] cols = line.split("\\|");
        Datum[] ret = new Datum[cols.length];
        for (int i = 0; i < cols.length; i++) {
            switch (schema[i].getType()) {
                case LONG:
                    ret[i] = new LONG(cols[i]);
                    break;
                case FLOAT:
                    ret[i] = new FLOAT(cols[i]);
                    break;
                case BOOL:
                    ret[i] = new BOOL(cols[i]);
                    break;
                case DATE:
//                    try {
//                        ret[i] = new DATE(cols[i]);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    break;
                case STRING:
                    ret[i] = new STRING(cols[i]);
                    break;
            }
        }
        return ret;
    }

    @Override
    public void reset() {
        input = new BufferedReader(getFileReader());
    }

    private FileReader getFileReader() {
        try {
            return new FileReader(new File(dataDir.getAbsolutePath() + "//" + tableName + ".dat"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ColumnSchema[] getSchema() {
        return schema;
    }

    public Long getTableSize() {
        return tableSize;
    }
}
