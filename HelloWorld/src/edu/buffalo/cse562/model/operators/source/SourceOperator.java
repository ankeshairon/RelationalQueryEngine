package edu.buffalo.cse562.model.operators.source;

import edu.buffalo.cse562.model.data.ResultSet;
import edu.buffalo.cse562.model.operatorabstract.Operator;

import java.util.ArrayList;
import java.util.List;

public class SourceOperator implements Operator {
    List<Operator> operators;
    ResultSet resultSet;

    public SourceOperator() {
        operators = new ArrayList<>();
    }

    @Override
    public void dataIn(ResultSet[] inputDataSet) {
        for (Operator operator : operators) {
            operator.dataIn(inputDataSet);
            resultSet = operator.dataOut();
        }
    }

    @Override
    public ResultSet dataOut() {
        return resultSet;
    }

    public void addSubOperator(Operator subOperator) {
        operators.add(subOperator);
    }

}
