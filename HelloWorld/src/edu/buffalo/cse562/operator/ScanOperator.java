package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.model.TableInfo;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

import java.io.*;
import java.util.List;

import static edu.buffalo.cse562.data.DatumUtils.getDatumOfTypeFromValue;

public class ScanOperator implements Operator {
    protected List<Integer> relevantColumnIndexes;
    private FileInputStream fileInputStream;
    public ColumnSchema[] schema;
    protected BufferedReader input;
    private Long tableSize;

    /**
     * requires table tableName and size only in tableInfo object if not passing null for finalSchema in the constructor
     */
    public ScanOperator(File dataDir, TableInfo tableInfo, ColumnSchema[] finalSchema) {
        this.schema = finalSchema;
        tableSize = tableInfo.getSize();
        makeSchema(tableInfo);
        try {
            fileInputStream = new FileInputStream(new File(dataDir.getAbsolutePath() + "//" + tableInfo.getTableName() + ".dat"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        reset();
    }

    private void makeSchema(TableInfo tableInfo) {
        if (schema == null) {
            final List<ColumnDefinition> allColumnDefinitions = tableInfo.getColumnDefinitions();
            relevantColumnIndexes = tableInfo.getColumnIndexesUsed();
            schema = new ColumnSchema[relevantColumnIndexes.size()];

            ColumnDefinition columnDefinition;
            for (int i = 0; i < relevantColumnIndexes.size(); i++) {
                columnDefinition = allColumnDefinitions.get(relevantColumnIndexes.get(i));
                schema[i] = new ColumnSchema(columnDefinition.getColumnName(), columnDefinition.getColDataType().getDataType());
                schema[i].setTableName(tableInfo.getTableName());
                schema[i].setTableAlias(tableInfo.getAlias());
            }
        }
    }

    @Override
    public Datum[] readOneTuple() {
        String line = null;
        try {
            if ((line = input.readLine()) == null) {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] cells = line.split("\\|");
        Datum[] tuple = new Datum[relevantColumnIndexes.size()];

        for (int i = 0; i < relevantColumnIndexes.size(); i++) {
            Integer index = relevantColumnIndexes.get(i);

            tuple[i] = getDatumOfTypeFromValue(schema[i].getType(), cells[index]);
        }
        return tuple;
    }

    @Override
    public void reset() {
        try {
            fileInputStream.getChannel().position(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        input = new BufferedReader(new InputStreamReader(fileInputStream));
    }

    @Override
    public ColumnSchema[] getSchema() {
        return schema;
    }

    public Long getProbableTableSize() {
        return tableSize;
    }
}
