/*Author - Ankesh Airon
 *ankeshai@buffalo.edu
 *Person no - 50096547
 *
 *Co-author: Dev Bharadwaj
*/
package edu.buffalo.cse562.model.operators;

import java.util.List;

import edu.buffalo.cse562.model.operatorabstract.AggregateOperator;
import edu.buffalo.cse562.model.operatorabstract.UnaryOperator;

public class ProjectionOperator extends UnaryOperator  {
    
	private List<String> columnNames;
	private List<AggregateOperator> aggregates;
	
	@Override
    public void dataIn() {

    }

    @Override
    public void dataOut() {

    }
}
