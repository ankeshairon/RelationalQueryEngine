package edu.buffalo.cse562.model.operators;

import edu.buffalo.cse562.model.data.Datum;
import edu.buffalo.cse562.model.operatorabstract.LeafOperator;

public class SourceOperator extends LeafOperator {

    @Override
    public void dataIn(Datum data) {
        super.dataIn(data);
    }

    @Override
    public Datum dataOut() {
        return super.dataOut();
    }
}
