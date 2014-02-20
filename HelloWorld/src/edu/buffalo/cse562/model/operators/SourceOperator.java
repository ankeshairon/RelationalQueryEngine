package edu.buffalo.cse562.model.operators;

import edu.buffalo.cse562.model.data.ResultSet;
import edu.buffalo.cse562.model.operatorabstract.LeafOperator;

public class SourceOperator implements LeafOperator {

    @Override
    public void dataIn(ResultSet data) {
    }

    @Override
    public ResultSet dataOut() {
        return null;
    }
}
