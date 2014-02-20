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

import java.util.List;

public class ProjectionOperator extends UnaryOperator {

    private List<String> columnNames;
    private List<AggregateOperator> aggregates;

    public ProjectionOperator(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public void dataIn(ResultSet data) {
        super.dataIn(data);
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

}
