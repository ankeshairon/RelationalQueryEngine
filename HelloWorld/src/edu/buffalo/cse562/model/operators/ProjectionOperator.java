/*Author - Ankesh Airon
 *ankeshai@buffalo.edu
 *Person no - 50096547
 *
 *Co-author: Dev Bharadwaj
*/
package edu.buffalo.cse562.model.operators;

import edu.buffalo.cse562.model.data.ResultSet;
import edu.buffalo.cse562.model.operatorabstract.AggregateOperator;
import edu.buffalo.cse562.model.operatorabstract.UnaryOperator;

import java.util.LinkedHashMap;
import java.util.List;

public class ProjectionOperator implements UnaryOperator {

    private Integer currentIndex;
    private LinkedHashMap<Integer, String> columnNames;
    private LinkedHashMap<Integer, AggregateOperator> aggregates;

    public ProjectionOperator() {
        currentIndex = 0;
    }

    @Override
    public void dataIn(ResultSet data) {
    }

    @Override
    public ResultSet dataOut() {
        //todo implement projection operator
        //make calls to data manipulating class

        if (aggregates == null && columnNames == null) {
            return getDataForAllColumns();
        } else if (aggregates == null) {
            return getDataForColumns();
        } else if (columnNames == null) {
            return getAggregatedData();
        } else {
            return getAggregatedDataForColumns();
        }
    }

    private ResultSet getAggregatedDataForColumns() {
        return null;
    }

    private ResultSet getAggregatedData() {
        return null;
    }

    private ResultSet getDataForColumns() {
        return null;
    }

    private ResultSet getDataForAllColumns() {
        return null;
    }

    public void setAggregates(List<AggregateOperator> aggregates) {
//        this.aggregates = aggregates;
    }

    public void setColumnNames(List<String> columnNames) {
//        this.columnNames = columnNames;
    }
}
