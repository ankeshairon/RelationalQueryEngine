package edu.buffalo.cse562.operator;

import net.sf.jsqlparser.statement.select.Limit;
import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;

public class LimitOperator implements Operator {
	
	Operator in;
	ColumnSchema[] outputSchema;
	long rowCount;
	long counter;
	
	public LimitOperator(Operator in,  Limit limit){
		this.outputSchema = in.getSchema();
		this.rowCount = limit.getRowCount();
	}
	
	@Override
	public Datum[] readOneTuple() {

		if(counter<rowCount){
			counter++;
			return in.readOneTuple();
		}
		return null;
	}

	@Override
	public void reset() {
		in.reset();
		counter = 0;
	}

	@Override
	public ColumnSchema[] getSchema() {
		return outputSchema;
	}

}
