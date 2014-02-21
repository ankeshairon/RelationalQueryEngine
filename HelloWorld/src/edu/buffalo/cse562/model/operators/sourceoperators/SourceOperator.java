package edu.buffalo.cse562.model.operators.sourceoperators;

import edu.buffalo.cse562.model.data.ResultSet;
import edu.buffalo.cse562.model.operatorabstract.Operator;

import java.util.ArrayList;
import java.util.List;

public class SourceOperator implements Operator {
    List<Operator> operators;

    public SourceOperator() {
        operators = new ArrayList<>();
    }

    @Override
    public void dataIn(ResultSet[] data) {

    }

    @Override
    public ResultSet dataOut() {
        return null;
    }

    public void addSubOperator(Operator subOperator) {
        operators.add(subOperator);
    }

}
