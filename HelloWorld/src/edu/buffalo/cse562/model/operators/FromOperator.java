package edu.buffalo.cse562.model.operators;

import edu.buffalo.cse562.datagrabber.DataGrabber;
import edu.buffalo.cse562.model.data.ResultSet;
import edu.buffalo.cse562.model.operatorabstract.LeafOperator;

public class FromOperator implements LeafOperator {

    private DataGrabber dataGrabber;
    private String sourceTableName;

    private ResultSet resultSet;

    public FromOperator(DataGrabber dataGrabber) {
        this.dataGrabber = dataGrabber;
    }

    @Override
    public void dataIn(ResultSet resultSet) {
        if (resultSet != null) {
            this.resultSet = resultSet;
        } else {
            this.resultSet = dataGrabber.getAllDataFromTable(sourceTableName);
        }
    }

    @Override
    public ResultSet dataOut() {
        return resultSet;
    }

    public void setSourceTableName(String sourceTableName) {
        this.sourceTableName = sourceTableName;
    }
}
