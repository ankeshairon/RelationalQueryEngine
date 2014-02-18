/*Author - Ankesh Airon
ankeshai@buffalo.edu
Person no - 50096547
*/
package edu.buffalo.cse562.model.operators;

import net.sf.jsqlparser.expression.Expression;

import java.util.List;

public class SourceOperator implements Operator {
    private List<String> tableName;
    private Expression whereCondition;


    public void setTableName(List<String> tableName) {
        this.tableName = tableName;
    }

    public void setWhereCondition(Expression whereCondition) {
        this.whereCondition = whereCondition;
    }

    @Override
    public void readOneTuple() {

    }

    @Override
    public void resetStream() {

    }
}
