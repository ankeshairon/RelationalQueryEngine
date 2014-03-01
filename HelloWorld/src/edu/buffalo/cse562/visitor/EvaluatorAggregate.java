package edu.buffalo.cse562.visitor;

import java.sql.Date;
import java.util.Stack;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.data.FLOAT;
import edu.buffalo.cse562.data.LONG;
import edu.buffalo.cse562.data.STRING;
import edu.buffalo.cse562.data.Datum.CastException;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.schema.Column;

public class EvaluatorAggregate extends AbstractExpressionVisitor {

	private Datum[] tuple;
	private ColumnSchema[] oldSchema;
	private Expression evalExpression;
	private Stack<Datum> literals;
	private Stack<String> symbols;
	
	public EvaluatorAggregate(Datum[] tuple, ColumnSchema[] oldSchema, Expression evalExpresssion) {
		this.tuple = tuple;
		this.oldSchema = oldSchema;
		this.evalExpression = evalExpression;
		literals = new Stack<>();
		symbols = new Stack<>();
	}
	
	public EvaluatorAggregate(Datum[] tuple, ColumnSchema[] oldSchema, Expression evalExpression, Stack<Datum> literals, Stack<String> symbols) {
		this.tuple = tuple;
		this.oldSchema = oldSchema;
		this.evalExpression = evalExpression;
		this.literals = literals;
		this.symbols = symbols;
	}

	public Datum executeStack() throws CastException {
		
		float floatData;
		String condition;
		float floatLeft;
		float floatRight;
		Datum dataLeft;
		Datum dataRight;
		
		while (!symbols.empty()) 
		{
			condition = symbols.pop();
			dataLeft = literals.pop();
			dataRight = literals.pop();
			floatLeft = dataLeft.toFLOAT();
			floatRight = dataRight.toFLOAT();
			
			switch (condition) {
			
			case "*"  : floatData = floatLeft * floatRight;
					    literals.push(new FLOAT(floatData));
					    break;
			case "/"  : floatData = floatLeft / floatRight;
					    literals.push(new FLOAT(floatData));
					    break;
			case "-"  : floatData = floatLeft - floatRight;
					    literals.push(new FLOAT(floatData));
					    break;
			case "+"  : floatData = floatLeft + floatRight;
					    literals.push(new FLOAT(floatData));
					    break;
			}

		}
		return literals.pop();
		
	}
	/*
	 * Binary Operators 
	 */
	
	@Override
	public void visit(Addition arg0) {
		symbols.push("+");
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		rightExpression.accept(new EvaluatorAggregate(tuple,oldSchema,evalExpression,literals,symbols));
		leftExpression.accept(new EvaluatorAggregate(tuple,oldSchema,evalExpression,literals,symbols));
	}
	
	@Override
	public void visit(Division arg0) {
		symbols.push("/");
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		rightExpression.accept(new EvaluatorAggregate(tuple,oldSchema,evalExpression,literals,symbols));
		leftExpression.accept(new EvaluatorAggregate(tuple,oldSchema,evalExpression,literals,symbols));
	}

	@Override
	public void visit(Multiplication arg0) {
		symbols.push("*");
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		rightExpression.accept(new EvaluatorAggregate(tuple,oldSchema,evalExpression,literals,symbols));
		leftExpression.accept(new EvaluatorAggregate(tuple,oldSchema,evalExpression,literals,symbols));
	}

	@Override
	public void visit(Subtraction arg0) {
		symbols.push("-");
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		rightExpression.accept(new EvaluatorAggregate(tuple,oldSchema,evalExpression,literals,symbols));
		leftExpression.accept(new EvaluatorAggregate(tuple,oldSchema,evalExpression,literals,symbols));
	}

	@Override
	public void visit(Parenthesis arg0) {
		Expression expr = arg0.getExpression();
		expr.accept(new EvaluatorAggregate(tuple,oldSchema,evalExpression,literals,symbols));
	}
	
	/*
	 * Leaf Nodes
	 */
	
	@Override
	public void visit(DoubleValue arg0) {
		FLOAT doubleVal = new FLOAT(arg0.toString());
		literals.push(doubleVal);
	}

	@Override
	public void visit(LongValue arg0) {
		LONG longVal = new LONG(arg0.toString());
		long nativeLong;
		try {
			nativeLong = longVal.toLONG();
			float nativeFloat = (float)nativeLong;
			FLOAT floatVal = new FLOAT(nativeFloat);
			literals.push(floatVal);
		} catch (CastException e) {
			e.printStackTrace();
		}
	}
/*
	@Override
	public void visit(DateValue arg0) {
        Date date = arg0.getValue();
        STRING dateVal = new STRING(date.toString());
		literals.push(dateVal);
	}

	@Override
	public void visit(StringValue arg0) {
		String str = arg0.toString();
		str = str.replaceAll("'", "");
		STRING stringVal = new STRING(str);
		literals.push(stringVal);
	}
*/	
	@Override
	public void visit(Column arg0) {
		
		String columnVal = arg0.getColumnName();
		Datum columnTupleVal = null;
		long nativeLong;
		float nativeFloat;
		FLOAT newFLOAT;
		int count = 0;
		for (ColumnSchema col: oldSchema) {
			if (col.getColName().equalsIgnoreCase(columnVal)) {
				columnTupleVal = tuple[count];
				if (columnTupleVal.getType() == Datum.type.LONG) {
					try {
						nativeLong = columnTupleVal.toLONG();
						nativeFloat = (float)nativeLong;
						newFLOAT = new FLOAT(nativeFloat);
						literals.push(newFLOAT);
					} catch (CastException e) {
						e.printStackTrace();
					}
				}
				else 
					literals.push(columnTupleVal);
				break;
			}
			count++;
		}
	}
}

