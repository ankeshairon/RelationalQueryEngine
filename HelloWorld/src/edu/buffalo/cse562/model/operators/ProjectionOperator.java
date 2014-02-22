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
import edu.buffalo.cse562.model.operators.utils.OperatorUtils;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;

public class ProjectionOperator implements UnaryOperator {

    private DataGrabber dataGrabber;
    private String tableName;

    private LinkedHashMap<Integer, String> columnNames;
    private LinkedHashMap<Integer, Function> aggregationFunctions;
    private Integer currentIndex;

    private ResultSet resultSet;

    public ProjectionOperator(DataGrabber dataGrabber, String tableName) {
        this.dataGrabber = dataGrabber;
        this.tableName = tableName;
        currentIndex = 0;
        columnNames = new LinkedHashMap<>();
        aggregationFunctions = new LinkedHashMap<>();
    }

    @Override
    public void dataIn(ResultSet[] inputDataSet) {
        //todo implement projection operator
        //make calls to resultSet manipulating class & populate resultset inside it

        if (aggregationFunctions.isEmpty() && columnNames.isEmpty()) {
            resultSet = inputDataSet[0];
        } else if (aggregationFunctions.isEmpty()) {
            resultSet = getDataOnlyForRelevantColumns(inputDataSet[0]);
        } else if (columnNames.isEmpty()) {
            resultSet = getAggregatedData(inputDataSet[0]);
        } else {
            resultSet = getAggregatedDataForColumns(inputDataSet[0]);
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
        aggregationFunctions.put(currentIndex, (Function) aggregationExpression);
        ++currentIndex;
    }

    private ResultSet getAggregatedDataForColumns(ResultSet inputDataSet) {
        return null;
    }

    private ResultSet getAggregatedData(ResultSet inputDataSet) {
        AggregateOperator aggregateOperator = new AggregateOperator(aggregationFunctions);
        aggregateOperator.dataIn(new ResultSet[]{inputDataSet});
        return aggregateOperator.dataOut();
    }

    private ResultSet getDataOnlyForRelevantColumns(ResultSet inputDataSet) {
        ListIterator<Tuple> iterator = inputDataSet.getTuplesListIteratorFromLastElement();
        ArrayList<String> newSchema = new ArrayList<>(columnNames.values());

        ArrayList<Tuple> newRowSet = new ArrayList<>();
        Tuple inputTuple;
        Tuple newTuple;

        List<Integer> indicesOfDataToPull = OperatorUtils.calculateIndicesOfTheseDataColumns(dataGrabber.getNamesOfAllColumnsForTable(tableName), newSchema);

        while (iterator.hasPrevious()) {
            inputTuple = iterator.previous();
            newTuple = getFilteredTuple(inputTuple, indicesOfDataToPull);
            newRowSet.add(newTuple);
        }

        return new ResultSet(newSchema, newRowSet);
    }


    private Tuple getFilteredTuple(Tuple inputTuple, List<Integer> indicesOfDataToPull) {
        Tuple newTuple = new Tuple();
        for (int i : indicesOfDataToPull) {
            newTuple.fields.add(inputTuple.fields.get(i));
        }
        return newTuple;
    }
}
