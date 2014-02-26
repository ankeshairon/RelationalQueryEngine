package edu.buffalo.cse562.visitor;

import java.sql.Date;
import java.util.Stack;

import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.schema.Column;
import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.data.Datum.CastException;
import edu.buffalo.cse562.data.FLOAT;
import edu.buffalo.cse562.data.LONG;
import edu.buffalo.cse562.data.STRING;
import edu.buffalo.cse562.schema.ColumnSchema;

public class Evaluator extends AbstractExpressionVisitor {

	
	Stack<Datum> literals;
	Stack<String> symbols; 
	private static boolean bool = true;
	ColumnSchema[] schema;
	Datum[] tuple;
	
	public Evaluator(ColumnSchema[] schema, Datum[] tuple){
		this.schema = schema;
		this.tuple = tuple;
		this.literals = new Stack<>();
		this.symbols = new Stack<>();
	}
	
	public Evaluator(ColumnSchema[] schema, Datum[] tuple, Stack<Datum> literals, Stack<String> symbols) {
		this.schema = schema;
		this.tuple = tuple;
		this.literals = literals;
		this.symbols = symbols;
	}
	
	public boolean getBool(){
		return bool;
	}

	public void executeStack() throws CastException {
		/*
		 * Execute Stack is not OR operator safe
		 */
		
		float floatData;
		int stringCheck;
		String condition;
		String stringLeft;
		String stringRight;
		Datum dataLeft;
		Datum dataRight;
		float floatLeft;
		float floatRight;
		while (!literals.empty() && !symbols.empty()) 
		{
			condition = symbols.pop();
			dataRight = literals.pop();
			dataLeft = literals.pop();
			if (dataRight.getType() == Datum.type.FLOAT  && dataLeft.getType() == Datum.type.FLOAT)
			{
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
				case "="  : bool = (floatLeft == floatRight)? true:false;
							break;
				case ">"  : if (floatLeft <= floatRight) bool = false;
						    break;
				case "<"  : if (floatLeft >= floatRight) bool = false;
						    break;
				case ">=" : if (floatLeft < floatRight) bool = false;
						    break;
				case "<=" : if (floatLeft > floatRight) bool = false;
							break;
				}
				if(!bool)
					break;
			}
				
			else if (dataRight.getType() == Datum.type.STRING  && dataLeft.getType() == Datum.type.STRING)
			{
				stringLeft = dataLeft.toSTRING();
				stringRight = dataRight.toSTRING();
				
				switch (condition) {
		
				case "="  : bool = (stringLeft.equalsIgnoreCase(stringRight))? true:false;
							break;
				case ">"  : stringCheck = stringLeft.compareTo(stringRight);
							if (stringCheck <= 0) bool = false;
							break;
				case "<"  : stringCheck = stringLeft.compareTo(stringRight);
							if (stringCheck >= 0) bool = false;
							break;
				case ">=" : stringCheck = stringLeft.compareTo(stringRight);
							if (stringCheck < 0) bool = false;
							break;
				case "<=" : stringCheck = stringLeft.compareTo(stringRight);
							if (stringCheck > 0) bool = false;
							break;
				}
				if (!bool)
					break;
			}
		}
	}
	
	/*
	 * Logical and Arithmetic operators
	 */
	
	@Override
	public void visit(Addition arg0) {
		
		symbols.push("+");
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		leftExpression.accept(new Evaluator(schema,tuple,literals,symbols));
		rightExpression.accept(new Evaluator(schema,tuple,literals,symbols));
		
	}

	@Override
	public void visit(Division arg0) {
		symbols.push("/");
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		
		leftExpression.accept(new Evaluator(schema,tuple,literals,symbols));
		rightExpression.accept(new Evaluator(schema,tuple,literals,symbols));
	}

	@Override
	public void visit(Multiplication arg0) {
		symbols.push("*");
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		
		leftExpression.accept(new Evaluator(schema,tuple,literals,symbols));
		rightExpression.accept(new Evaluator(schema,tuple,literals,symbols));
		
}

	@Override
	public void visit(Subtraction arg0) {
		symbols.push("-");
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		
		leftExpression.accept(new Evaluator(schema,tuple,literals,symbols));
		rightExpression.accept(new Evaluator(schema,tuple,literals,symbols));
}

	@Override
	public void visit(AndExpression arg0) {
		symbols.push("AND");
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		
		leftExpression.accept(new Evaluator(schema,tuple,literals,symbols));
		rightExpression.accept(new Evaluator(schema,tuple,literals,symbols));
		
	}

	@Override
	public void visit(EqualsTo arg0) {
		symbols.push("=");
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		
		leftExpression.accept(new Evaluator(schema,tuple,literals,symbols));
		rightExpression.accept(new Evaluator(schema,tuple,literals,symbols));
	}

	@Override
	public void visit(GreaterThan arg0) {
		symbols.push(">");
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		
		leftExpression.accept(new Evaluator(schema,tuple,literals,symbols));
		rightExpression.accept(new Evaluator(schema,tuple,literals,symbols));
	}

	@Override
	public void visit(GreaterThanEquals arg0) {
		symbols.push(">=");
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		 
		leftExpression.accept(new Evaluator(schema,tuple,literals,symbols));
		rightExpression.accept(new Evaluator(schema,tuple,literals,symbols));
	}

	@Override
	public void visit(MinorThan arg0) {
		symbols.push("<");
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		
		leftExpression.accept(new Evaluator(schema,tuple,literals,symbols));
		rightExpression.accept(new Evaluator(schema,tuple,literals,symbols));

	}

	@Override
	public void visit(MinorThanEquals arg0) {
		symbols.push("<=");
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		
		leftExpression.accept(new Evaluator(schema,tuple,literals,symbols));
		rightExpression.accept(new Evaluator(schema,tuple,literals,symbols));
	}
	/*
	 *literals or Leaf Nodes of the recursion tree
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

	@Override
	public void visit(Column arg0) {
			
		String columnVal = arg0.getColumnName();
		Datum columnTupleVal = null;
		int count = 0;
		for (ColumnSchema col: schema) {
			if(col.colName.equals(columnVal)) {
				columnTupleVal = tuple[count];
				literals.push(columnTupleVal);
				break;
			}
			count++;
		}
	}
	
}
