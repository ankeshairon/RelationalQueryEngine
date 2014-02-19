/*Author - Ankesh Airon
 *ankeshai@buffalo.edu
 *Person no - 50096547
 *
 *Co-Author - Dev Bharadwaj
*/
package edu.buffalo.cse562.model.operators;

import net.sf.jsqlparser.expression.Expression;

import java.util.List;

import edu.buffalo.cse562.model.operatorabstract.LeafOperator;

public class SourceOperator extends LeafOperator {
    private List<String> tableName;

    public void setTableName(List<String> tableName) {
        this.tableName = tableName;
    }


    @Override
    public void dataIn() {

    }

    @Override
    public void dataOut() {

    }
}
