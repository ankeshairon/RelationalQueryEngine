/*Author - Ankesh Airon
 *ankeshai@buffalo.edu
 *Person no - 50096547
 *
 *Co-Author: Dev Bharadwaj
*/
package edu.buffalo.cse562.model.operators;

import edu.buffalo.cse562.model.data.ResultSet;
import edu.buffalo.cse562.model.operatorabstract.UnaryOperator;
import net.sf.jsqlparser.expression.Expression;

public class SelectionOperator implements UnaryOperator {

    private Expression whereCondition;
	
	public void setWhereCondition(Expression whereCondition) {
		this.whereCondition = whereCondition;
	}

    @Override
    public ResultSet dataOut() {
        return super.dataOut();
    }

    @Override
    public void dataIn(ResultSet data) {
        super.dataIn(data);
    }
}
