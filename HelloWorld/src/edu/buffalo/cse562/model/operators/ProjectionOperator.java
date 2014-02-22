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
import edu.buffalo.cse562.model.operators.aggregate.AggregateOperator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static edu.buffalo.cse562.model.operators.utils.OperatorUtils.calculateIndicesOfTheseDataColumns;

public class ProjectionOperator implements UnaryOperator {

    private DataGrabber dataGrabber;
    private String tableName;

    private List<String> columnNames;
    private List<Function> aggregationFunctions;
    private List<Integer> indexesOfFunctionsInProjectionList;
    private Integer currentIndex;

    private ResultSet resultSet;

    public ProjectionOperator(DataGrabber dataGrabber, String tableName) {
        this.dataGrabber = dataGrabber;
        this.tableName = tableName;
        currentIndex = 0;
        columnNames = new ArrayList<>();
        indexesOfFunctionsInProjectionList = new ArrayList<>();
        aggregationFunctions = new ArrayList<>();
    }

    @Override
    public void dataIn(ResultSet[] inputDataSet) {
        if (aggregationFunctions.isEmpty() && columnNames.isEmpty()) {
            resultSet = inputDataSet[0];
        } else if (aggregationFunctions.isEmpty()) {
            resultSet = getDataForRelevantColumnsOnly(inputDataSet[0]);
        } else if (columnNames.isEmpty()) {
            resultSet = getDataForAggregationsOnly(inputDataSet[0]);
        } else {
            resultSet = getDataForColumnsAndAggregations(inputDataSet[0]);
        }

    }

    @Override
    public ResultSet dataOut() {
        return resultSet;
    }

    public void addProjectionAttribute(String columnName) {
        columnNames.add(columnName);
        ++currentIndex;
    }

    public void addProjectionAttribute(Expression projectionExpression) {
        if (projectionExpression instanceof Function) {
            aggregationFunctions.add((Function) projectionExpression);
            indexesOfFunctionsInProjectionList.add(currentIndex);
        } else {
            columnNames.add(projectionExpression.toString());
        }
        ++currentIndex;
    }

    private ResultSet getDataForColumnsAndAggregations(ResultSet inputDataSet) {
        ResultSet destinationDataSet = getRowsOfDataForColumnsInOrder(inputDataSet, 1);
        ResultSet indexedDataSet = getDataForAggregationsOnly(inputDataSet);

        mergePartialLists2To1(destinationDataSet.getSchema(), indexedDataSet.getSchema());
        mergePartialLists2To1(destinationDataSet.getTuples().get(0).fields, indexedDataSet.getTuples().get(0).fields);

        return destinationDataSet;
    }

    private void mergePartialLists2To1(List destinationList, List indexedList) {
        ListIterator indexedListIterator = indexedList.listIterator();
        for (Integer targetIndex : indexesOfFunctionsInProjectionList) {
            destinationList.add(targetIndex, indexedListIterator.next());
        }
    }

    private ResultSet getDataForAggregationsOnly(ResultSet inputDataSet) {
        AggregateOperator aggregateOperator = new AggregateOperator(aggregationFunctions);
        aggregateOperator.dataIn(new ResultSet[]{inputDataSet});
        return aggregateOperator.dataOut();
    }

    private ResultSet getDataForRelevantColumnsOnly(ResultSet inputDataSet) {
        return getRowsOfDataForColumnsInOrder(inputDataSet, -1);
    }

    private ResultSet getRowsOfDataForColumnsInOrder(ResultSet inputDataSet, Integer noOfRows) {
        List<Integer> indicesOfDataToPull = calculateIndicesOfTheseDataColumns(dataGrabber.getNamesOfAllColumnsForTable(tableName), columnNames);
        ArrayList<Tuple> newRowSet = new ArrayList<>();

        if (noOfRows < 0) {
            populateDataForAllRows(inputDataSet, newRowSet, indicesOfDataToPull);
        } else {
            populateLastRowOfData(inputDataSet, newRowSet, indicesOfDataToPull, noOfRows);
        }
        return new ResultSet(columnNames, newRowSet);
    }

    private void populateLastRowOfData(ResultSet inputDataSet, ArrayList<Tuple> newRowSet, List<Integer> indicesOfDataToPull, Integer noOfRows) {
        Tuple inputTuple;
        Tuple newTuple;
        ListIterator<Tuple> iterator = inputDataSet.getTuplesListIteratorFromLastElement();
        if (iterator.hasPrevious() && noOfRows-- != 0) {
            inputTuple = iterator.previous();
            newTuple = getFilteredTuple(inputTuple, indicesOfDataToPull);
            newRowSet.add(newTuple);
        }
    }

    private void populateDataForAllRows(ResultSet inputDataSet, ArrayList<Tuple> newRowSet, List<Integer> indicesOfDataToPull) {
        Tuple inputTuple;
        Tuple newTuple;
        ListIterator<Tuple> iterator = inputDataSet.getTuplesListIteratorFromFirstElement();
        while (iterator.hasNext()) {
            inputTuple = iterator.next();
            newTuple = getFilteredTuple(inputTuple, indicesOfDataToPull);
            newRowSet.add(newTuple);
        }
    }


    private Tuple getFilteredTuple(Tuple inputTuple, List<Integer> indicesOfDataToPull) {
        Tuple newTuple = new Tuple();
        for (int i : indicesOfDataToPull) {
            newTuple.fields.add(inputTuple.fields.get(i));
        }
        return newTuple;
    }
}
