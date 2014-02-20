/*
 * @author: Dev Bharadwaj
 * Join will take two datums and one condition(optional) and produce and output 
 *
 */

package edu.buffalo.cse562.model.operators;

import edu.buffalo.cse562.mock.Datum;
import edu.buffalo.cse562.model.operatorabstract.BinaryOperator;
import net.sf.jsqlparser.statement.select.Join;

public class JoinOperator extends BinaryOperator{

    Datum data;
    private Join join;

    public JoinOperator(Join join) {
        this.join = join;
    }
    // Expression condition;


    @Override
    public void dataIn(Datum data) {
        this.data = data;
    }

    @Override
    public Datum dataOut() {
        // todo actually Join the damn data -> Check the type of join and extract condition if not natural join

        return data;
    }
}
