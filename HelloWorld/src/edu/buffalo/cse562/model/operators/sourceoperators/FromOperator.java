package edu.buffalo.cse562.model.operators.sourceoperators;

import edu.buffalo.cse562.datagrabber.DataGrabber;
import edu.buffalo.cse562.model.data.ResultSet;
import edu.buffalo.cse562.model.operatorabstract.LeafOperator;

public class FromOperator implements LeafOperator {

    private DataGrabber dataGrabber;
    private String sourceTableName;

    public FromOperator(DataGrabber dataGrabber) {
        this.dataGrabber = dataGrabber;
    }

    public void setSourceTableName(String sourceTableName) {
        this.sourceTableName = sourceTableName;
    }

    @Override
    public void dataIn(ResultSet[] resultSet) {
        //always supposed to receive null
        throw new UnsupportedOperationException("From operator does not accept any data");
    }

    @Override
    public ResultSet dataOut() {
        return dataGrabber.getAllDataFromTable(sourceTableName);
    }
}
