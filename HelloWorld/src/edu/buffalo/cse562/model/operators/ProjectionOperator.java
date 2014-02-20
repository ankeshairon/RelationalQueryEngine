/*Author - Ankesh Airon
 *ankeshai@buffalo.edu
 *Person no - 50096547
 *
 *Co-author: Dev Bharadwaj
*/
package edu.buffalo.cse562.model.operators;

import edu.buffalo.cse562.model.data.Datum;
import edu.buffalo.cse562.model.operatorabstract.AggregateOperator;
import edu.buffalo.cse562.model.operatorabstract.UnaryOperator;

import java.util.List;

public class ProjectionOperator extends UnaryOperator  {
    
	private List<String> columnNames;
	private List<AggregateOperator> aggregates;

    public ProjectionOperator(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public void dataIn(Datum data) {
        super.dataIn(data);
    }

    @Override
    public Datum dataOut() {
        if (aggregates == null && columnNames == null) {
            return getDataForAllColumns();
        } else if (aggregates == null)

            //if column names & aggregates is null, it shud return data for all columns
            //if only aggregates is empty, then get data for specified columns
            //if only column names is empty, return data based on aggregates
            //if none are empty, return selected columns with the aggregates
            return super.dataOut();

    }

}
