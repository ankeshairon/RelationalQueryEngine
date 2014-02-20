/*Author - Ankesh Airon
 *ankeshai@buffalo.edu
 *Person no - 50096547
 *
 *Co-author: Dev Bharadwaj
*/
package edu.buffalo.cse562.model.operators;

import edu.buffalo.cse562.mock.Datum;
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
        //if column names & aggregates is null, it shud return data for all columns
        //if aggregates is empty, apply them to the list of data
        //
        return super.dataOut();

    }

}
