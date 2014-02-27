package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.Main;
import edu.buffalo.cse562.data.*;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

public class ScanOperator implements Operator {
    BufferedReader input;
    String tableName;
    public ColumnSchema[] schema;
    HashMap<String, List<ColumnDefinition>> tables;
    private String dataDir;

    public ScanOperator(String dataDir, String tableName, HashMap<String, List<ColumnDefinition>> tables) {
        this.tables = tables;
        this.tableName = tableName;
        this.dataDir = dataDir;
        makeSchema(tableName);
        reset();
    }

    public void makeSchema(String tbl) {
        List<ColumnDefinition> colDefns = tables.get(tbl.toLowerCase());
        schema = new ColumnSchema[colDefns.size()];
        int i = 0;
        for (ColumnDefinition cd : colDefns) {
            schema[i] = new ColumnSchema(cd.getColumnName(), cd.getColDataType().getDataType());
            i++;
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
        input = new BufferedReader(getInputStreamReader());
    }

    private InputStreamReader getInputStreamReader() {
        return new InputStreamReader(Main.class.getResourceAsStream(System.getProperty("user.dir") + "/" + dataDir + "/" + tableName + ".dat"));
    }

    @Override
    public ColumnSchema[] getSchema() {
        return schema;
    }

}
