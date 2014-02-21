/*
 * @author: Dev Bharadwaj
 * Join will take two datums and one condition(optional) and produce and output 
 *
 */

package edu.buffalo.cse562.model.operators.sourceoperators;

import edu.buffalo.cse562.model.data.ResultSet;
import edu.buffalo.cse562.model.operatorabstract.BinaryOperator;
import net.sf.jsqlparser.statement.select.Join;

public class JoinOperator implements BinaryOperator {

    ResultSet[] resultSet;
    private Join join;

    public JoinOperator(Join join) {
        this.join = join;
    }
    // Expression condition;


    @Override
    public void dataIn(ResultSet[] data) {
        this.resultSet = data;
    }

    @Override
    public ResultSet dataOut() {
        // todo actually Join the damn resultSet -> Check the type of join and extract condition if not natural join

        return null;
    }
}
