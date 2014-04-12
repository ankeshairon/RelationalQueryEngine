package edu.buffalo.cse562.operator;

import net.sf.jsqlparser.statement.select.Limit;
import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;

public class LimitOperator implements Operator {
	
	Operator in;
	ColumnSchema[] outputSchema;
	final long rowCount;
	long counter;
	
	public LimitOperator(Operator in,  Limit limit){
		this.in = in;
		this.outputSchema = in.getSchema();
		this.rowCount = limit.getRowCount();
		counter = 0;  
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

    @Override
    public Long getProbableTableSize() {
        //todo add a smarter logic based on tuple size
        return rowCount * 100;
    }

}
