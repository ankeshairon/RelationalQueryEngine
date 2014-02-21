package edu.buffalo.cse562.datagrabber;

import edu.buffalo.cse562.model.data.ResultSet;
import edu.buffalo.cse562.model.data.Tuple;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataGrabber {

    private String dataFolder;
    private Map<String, ArrayList<ColumnDefinition>> tables;

    public DataGrabber(String dataFolder) {
        this.dataFolder = dataFolder;
        this.tables = new LinkedHashMap<>();
    }

    public void addTable(String tableName, ArrayList<ColumnDefinition> columnDefinitions) {
        tables.put(tableName, columnDefinitions);
    }

    /*
     * Author: Subhendu Saha
     * This method takes tablename as parameter
     * and returns the raw in-memory data to caller
     */
    public ResultSet getAllDataFromTable(String tableName) {

        ArrayList<ColumnDefinition> schema = tables.get(tableName);
        ArrayList<Tuple> tuples = new ArrayList<>();


        try (
                BufferedReader reader = new BufferedReader(new FileReader(new File(dataFolder + tableName + ".dat")));
        ) {

            String line;
            String[] dataWords;

            while ((line = reader.readLine()) != null) {
                dataWords = line.split("\\|");
                Tuple tuple = new Tuple();

                for (String dataWord : dataWords) {
                    tuple.fields.add(dataWord);
                }
                tuples.add(tuple);
            }
            return new ResultSet(schema, tuples);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
