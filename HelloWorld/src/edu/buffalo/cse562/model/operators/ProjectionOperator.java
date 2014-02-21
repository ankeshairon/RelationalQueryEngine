/*Author - Ankesh Airon
 *ankeshai@buffalo.edu
 *Person no - 50096547
 *
 *Co-author: Dev Bharadwaj
*/
package edu.buffalo.cse562.model.operators;

import edu.buffalo.cse562.datagrabber.DataGrabber;
import edu.buffalo.cse562.model.data.ResultSet;
import edu.buffalo.cse562.model.data.Tuple;
import edu.buffalo.cse562.model.operatorabstract.UnaryOperator;
import net.sf.jsqlparser.expression.Expression;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;

public class ProjectionOperator implements UnaryOperator {

    private DataGrabber dataGrabber;
    private String tableName;

    private LinkedHashMap<Integer, String> columnNames;
    private LinkedHashMap<Integer, Expression> aggregateExpressions;
    private Integer currentIndex;

    private ResultSet resultSet;

    public ProjectionOperator(DataGrabber dataGrabber, String tableName) {
        this.dataGrabber = dataGrabber;
        this.tableName = tableName;
        currentIndex = 0;
        columnNames = new LinkedHashMap<>();
        aggregateExpressions = new LinkedHashMap<>();
    }

    @Override
    public void dataIn(ResultSet inputDataSet) {
        //todo implement projection operator
        //make calls to resultSet manipulating class & populate resultset inside it

        if (aggregateExpressions.isEmpty() && columnNames.isEmpty()) {
            resultSet = inputDataSet;
        } else if (aggregateExpressions.isEmpty()) {
            resultSet = getDataOnlyForRelevantColumns(inputDataSet);
        } else if (columnNames.isEmpty()) {
            resultSet = getAggregatedData(inputDataSet);
        } else {
            resultSet = getAggregatedDataForColumns(inputDataSet);
        }

    }

    @Override
    public ResultSet dataOut() {
        return resultSet;
    }

    public void addProjectionAttribute(String columnName) {
        columnNames.put(currentIndex, columnName);
        ++currentIndex;
    }

    public void addProjectionAttribute(Expression aggregationExpression) {
        aggregateExpressions.put(currentIndex, aggregationExpression);
        ++currentIndex;
    }

    private ResultSet getAggregatedDataForColumns(ResultSet inputDataSet) {
        return null;
    }

    private ResultSet getAggregatedData(ResultSet inputDataSet) {
        AggregateOperator aggregateOperator = new AggregateOperator(aggregateExpressions);
        aggregateOperator.dataIn(inputDataSet);
        return aggregateOperator.dataOut();
    }

    private ResultSet getDataOnlyForRelevantColumns(ResultSet inputDataSet) {
        ListIterator<Tuple> iterator = inputDataSet.getTuplesListIteratorFromLastElement();
        ArrayList<Tuple> newRowSet = new ArrayList<>();
        Tuple inputTuple;
        Tuple newTuple;

        while (iterator.hasPrevious()) {
            inputTuple = iterator.previous();
            newTuple = getFilteredTuple(inputTuple, calculateIndicesOfDataToPull());
            newRowSet.add(newTuple);
        }

        ArrayList<String> schema = new ArrayList<>(columnNames.values());
        return new ResultSet(schema, newRowSet);
    }


    private Tuple getFilteredTuple(Tuple inputTuple, List<Integer> indicesOfDataToPull) {
        Tuple newTuple = new Tuple();
        for (int i : indicesOfDataToPull) {
            newTuple.fields.add(inputTuple.fields.get(i));
        }
        return newTuple;
    }

    private List<Integer> calculateIndicesOfDataToPull() {
        List<Integer> indices = new ArrayList<>();
        List<String> allColumnNamesInTable = dataGrabber.getNamesOfAllColumnsForTable(tableName);

        Integer indexOfColumn;
        for (String nameOfColumnToBeProjected : allColumnNamesInTable) {
            indexOfColumn = allColumnNamesInTable.indexOf(nameOfColumnToBeProjected);
            indices.add(indexOfColumn);
        }
        return indices;
    }
}
