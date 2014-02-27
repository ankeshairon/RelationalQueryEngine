package edu.buffalo.cse562.operator;

import java.util.List;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;

public class OrderByOperator implements Operator {

	Operator input;
	ColumnSchema[] schema;
	List<ColumnSchema> orderByColumns;
	List<ColumnSchema> groupByColumns;
	
	public OrderByOperator(Operator input, ColumnSchema[] schema, List<ColumnSchema> orderByColumns, List<ColumnSchema> groupByColumns) {
		this.input = input;
		this.schema = schema;
		this.orderByColumns = orderByColumns;
		this.groupByColumns = groupByColumns;
	}
	
	@Override
	public Datum[] readOneTuple() {
		Datum tuple[];
		while ((tuple = input.readOneTuple()) != null) {
			
		}
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ColumnSchema[] getSchema() {
		// TODO Auto-generated method stub
		return null;
	}

}
