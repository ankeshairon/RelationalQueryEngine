package edu.buffalo.cse562.datagrabber;

import edu.buffalo.cse562.parser.datavisitors.ExpressionDataVisitorImpl;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataGrabber {

    private String dataFolder;
    private Map<String, ArrayList<String>> tables;

    public DataGrabber(String dataFolder) {
        this.dataFolder = dataFolder;
        this.tables = new HashMap<>();
    }

    public void addTable(String tableName, ArrayList<String> columnDefinitions) {
        tables.put(tableName, columnDefinitions);
    }

    public String retrieveItemsFrom(List<SelectItem> selectItems, FromItem fromItem, Expression whereCondition) {
        StringBuilder results = new StringBuilder();
        String dataFileName = fromFileNamed(fromItem);
        List<Integer> indicesOfDataToPull = calculateIndicesOfDataToPull(fromItem, selectItems);

        //TODO implement where clause & perhaps come up with a better place to do this
        ExpressionDataVisitorImpl expressionEvaluator = getExpressionEvaluator(whereCondition);

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(dataFileName)));

            String tuple;
            while ((tuple = bufferedReader.readLine()) != null) {
                populateRelevantDataInResults(results, indicesOfDataToPull, tuple);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Sorry, unable to find the file " + dataFileName);
        } catch (IOException e) {
            System.out.println("Oops! Something went wrong while reading the data file");
        }
        return results.toString();
    }

    private ExpressionDataVisitorImpl getExpressionEvaluator(Expression whereCondition) {
        ExpressionDataVisitorImpl expressionEvaluator = new ExpressionDataVisitorImpl();
        whereCondition.accept(expressionEvaluator);
        return expressionEvaluator;
    }

    private void populateRelevantDataInResults(StringBuilder results, List<Integer> indicesOfDataToPull, String tuple) {
        String[] tupleValues = tuple.split("\\|");
        for (Integer index : indicesOfDataToPull) {
            results.append(tupleValues[index]);
            results.append("|");
        }
        results.setCharAt(results.length() - 1, '\n');
    }

    private List<Integer> calculateIndicesOfDataToPull(FromItem fromItem, List<SelectItem> selectItems) {
        List<Integer> indices = new ArrayList<>();
        List<String> columnNames = tables.get(getTableName(fromItem));

        Integer indexOfColumn;
        for (SelectItem selectItem : selectItems) {
            indexOfColumn = columnNames.indexOf(selectItem.toString());
            indices.add(indexOfColumn);
        }

        return indices;
    }

    private String fromFileNamed(FromItem fromItem) {
        return dataFolder + getTableName(fromItem) + ".dat";
    }

    private String getTableName(FromItem fromItem) {
        return ((Table) fromItem).getName();
    }
}
