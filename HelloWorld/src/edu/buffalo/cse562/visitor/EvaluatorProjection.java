package edu.buffalo.cse562.visitor;

import java.util.List;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;

public class EvaluatorProjection extends AbstractExpressionVisitor {

    static ColumnSchema[] inputSchema;
    List<ColumnSchema> outputSchema;
    List<Integer> indexes;
	Expression expression;
	String alias;
	static int counter = 0;
	
	public EvaluatorProjection(Expression expression, String alias, ColumnSchema[] inputSchema, 
			List<ColumnSchema> outputSchema, List<Integer> indexes) {
		this.expression = expression;
		this.alias = alias;
		this.inputSchema = inputSchema;
		this.outputSchema = outputSchema;
		this.indexes = indexes;
	}
	
	public EvaluatorProjection(Expression expression, ColumnSchema[] inputSchema, List<ColumnSchema> outputSchema) {
		
	}
	
	@Override
	public void visit(Function arg0) {
		String aggregate = arg0.getName();
		ExpressionList expressionList = arg0.getParameters();
		boolean isAllColumns = arg0.isAllColumns();
		boolean isDistinct = arg0.isDistinct();
		
		/*
		 * Aggregates don't follow the pull model 
		 * We need to implement the is_done function()
		 * This is to be a blocking method.
		 */
		System.out.println(aggregate + ", All columns " + isAllColumns + ", Distinct " + isDistinct);
		List<Expression> expr;
		while ((expr = expressionList.getExpressions()) != null) {
			for (Expression e: expr){
			System.out.println(e);
			}
		}

	}

	@Override
	public void visit(DoubleValue arg0) {
		// TODO Auto-generated method stub
		super.visit(arg0);
	}

	@Override
	public void visit(LongValue arg0) {
		// TODO Auto-generated method stub
		super.visit(arg0);
	}

	@Override
	public void visit(Addition arg0) {
		// TODO Auto-generated method stub
		super.visit(arg0);
	}

	@Override
	public void visit(Division arg0) {
		// TODO Auto-generated method stub
		super.visit(arg0);
	}

	@Override
	public void visit(Multiplication arg0) {
		// TODO Auto-generated method stub
		super.visit(arg0);
	}

	@Override
	public void visit(Subtraction arg0) {
		// TODO Auto-generated method stub
		super.visit(arg0);
	}

	@Override
	public void visit(Parenthesis arg0) {
		// TODO Auto-generated method stub
		super.visit(arg0);
	}

	@Override
	public void visit(Column arg0) {
		for (int i = 0; i < inputSchema.length; i++) {
			if (arg0.getColumnName().equalsIgnoreCase(inputSchema[i].getColName())) {
				indexes.add(i);
                ColumnSchema columnSchema = new ColumnSchema(inputSchema[i].getColName(), inputSchema[i].getType());
                columnSchema.setAlias(inputSchema[i].getAlias());
                outputSchema.add(columnSchema);
                counter++;
                break;
             }
         }

	}
}