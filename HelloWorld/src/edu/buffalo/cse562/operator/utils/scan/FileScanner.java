package edu.buffalo.cse562.operator.utils.scan;

import edu.buffalo.cse562.indexer.model.TableIndexingInfo;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

import java.io.*;
import java.util.List;

public class FileScanner {

    private ColumnSchema[] schema;
    private BufferedReader input;

    public FileScanner(File dataDir, TableIndexingInfo tableIndexingInfo) {
        makeSchema(tableIndexingInfo.getColumnDefinitions());
        try {
            input = new BufferedReader(new InputStreamReader(new FileInputStream(new File(dataDir.getAbsolutePath() + "//" + tableIndexingInfo.getTableName() + ".dat"))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String readOneLine() {
        String line = null;

        try {
            line = input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public ColumnSchema[] getSchema() {
        return schema;
    }

    private ColumnSchema[] makeSchema(List<ColumnDefinition> columnDefinitions) {
        schema = new ColumnSchema[columnDefinitions.size()];

        ColumnDefinition columnDef;
        for (int i = 0; i < columnDefinitions.size(); i++) {
            columnDef = columnDefinitions.get(i);
            schema[i] = new ColumnSchema(columnDef.getColumnName(), columnDef.getColDataType().getDataType());
        }
        return schema;
    }
}