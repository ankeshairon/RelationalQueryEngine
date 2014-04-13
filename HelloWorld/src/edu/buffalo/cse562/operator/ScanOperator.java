package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.data.*;
import edu.buffalo.cse562.model.TableInfo;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

import java.io.*;
import java.util.List;

public class ScanOperator implements Operator {
    public ColumnSchema[] schema;

    private Long tableSize;
    private BufferedReader input;
    private FileInputStream fileInputStream;
    private List<Integer> relevantColumnIndexes;

    /**
     *   requires table name and size only in tableInfo object if not passing null for finalSchema in the constructor
    */
    public ScanOperator(File dataDir, TableInfo tableInfo, ColumnSchema[] finalSchema) {
        this.schema = finalSchema;
        tableSize = tableInfo.getSize();
        makeSchema(tableInfo);
        try {
            fileInputStream = new FileInputStream(new File(dataDir.getAbsolutePath() + "//" + tableInfo.getName() + ".dat"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        reset();
    }

    public void makeSchema(TableInfo tableInfo) {
        if (schema == null) {
            final List<ColumnDefinition> allColumnDefinitions = tableInfo.getColumnDefinitions();
            relevantColumnIndexes = tableInfo.getColumnIndexesUsed();
            schema = new ColumnSchema[relevantColumnIndexes.size()];

            ColumnDefinition columnDefinition;
            for (int i = 0; i < relevantColumnIndexes.size(); i++) {
                columnDefinition = allColumnDefinitions.get(relevantColumnIndexes.get(i));
                schema[i] = new ColumnSchema(columnDefinition.getColumnName(), columnDefinition.getColDataType().getDataType());
                schema[i].setTableName(tableInfo.getName());
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

            switch (schema[i].getType()) {
                case LONG:
                    tuple[i] = new LONG(cells[index]);
                    break;
                case FLOAT:
                    tuple[i] = new FLOAT(cells[index]);
                    break;
                case BOOL:
                    tuple[i] = new BOOL(cells[index]);
                    break;
                case DATE:
//                    try {
//                        tuple[i] = new DATE(cells[index]);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    break;
                case STRING:
                    tuple[i] = new STRING(cells[index]);
                    break;
            }
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
