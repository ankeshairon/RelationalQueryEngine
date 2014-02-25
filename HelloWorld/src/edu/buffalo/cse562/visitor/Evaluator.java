package edu.buffalo.cse562.visitor;

import java.sql.Date;

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
import edu.buffalo.cse562.data.STRING;
import edu.buffalo.cse562.schema.ColumnSchema;

public class Evaluator extends AbstractExpressionVisitor {

	private static boolean bool = true;
	private static float floatRight = -1;
	private static float floatLeft = -1;
	private static String strRight = "";
	private static String strLeft = "";
	private static boolean right = false;
	private static boolean left = false;
	
	ColumnSchema[] schema;
	Datum[] tuple;
	
	public Evaluator(ColumnSchema[] schema, Datum[] tuple){
		this.schema = schema;
		this.tuple = tuple;
	}
	
	public Evaluator(ColumnSchema[] schema, Datum[] tuple, boolean left, boolean right) {
		this.schema = schema;
		this.tuple = tuple;
		this.left = left;
		this.right = right;
	}
	
	public boolean getBool(){
		return bool;
	}

	/*
	 * Logical and Arithmetic operators
	 */
	
	@Override
	public void visit(Addition arg0) {
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		leftExpression.accept(new Evaluator(schema,tuple));
		rightExpression.accept(new Evaluator(schema,tuple));
		
		if (left) {
			if (floatLeft != -1 && floatRight != -1) {
				floatLeft = floatLeft + floatRight;
			}	
		
			if (strLeft != "" && strRight != "") {
				strLeft = strLeft + strRight;
			}
		}
		else if (right) {
			if (floatLeft != -1 && floatRight != -1) {
				floatRight = floatLeft + floatRight;
			}	
		
			if (strLeft != "" && strRight != "") {
				strRight = strLeft + strRight;
			}
			
		}

               		
	}

	@Override
	public void visit(Division arg0) {
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		
		leftExpression.accept(new Evaluator(schema,tuple));
		rightExpression.accept(new Evaluator(schema,tuple));
		
		if (left) {
			if (floatLeft != -1 && floatRight != -1) {
				floatLeft = floatLeft / floatRight;
			}	
		
			if (strLeft != "" && strRight != "") {
				throw new IllegalArgumentException(); 
			}
		}
		else if (right) {
			if (floatLeft != -1 && floatRight != -1) {
				floatRight = floatLeft / floatRight;
			}	
		
			if (strLeft != "" && strRight != "") {
				throw new IllegalArgumentException();
			}
			
		}

               		
			
	}

	@Override
	public void visit(Multiplication arg0) {
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		
		leftExpression.accept(new Evaluator(schema,tuple));
		rightExpression.accept(new Evaluator(schema,tuple));

		if (left) {
			if (floatLeft != -1 && floatRight != -1) {
				floatLeft = floatLeft * floatRight;
			}	
		
			if (strLeft != "" && strRight != "") {
				throw new IllegalArgumentException();
			}
		}
		else if (right) {
			if (floatLeft != -1 && floatRight != -1) {
				floatRight = floatLeft * floatRight;
			}	
		
			if (strLeft != "" && strRight != "") {
				throw new IllegalArgumentException();
			}
			
		}
		
}

	@Override
	public void visit(Subtraction arg0) {
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		
		leftExpression.accept(new Evaluator(schema,tuple));
		rightExpression.accept(new Evaluator(schema,tuple));
		
		if (left) {
			if (floatLeft != -1 && floatRight != -1) {
				floatLeft = floatLeft - floatRight;
			}	
		
			if (strLeft != "" && strRight != "") {
				throw new IllegalArgumentException();
			}
		}
		else if (right) {
			if (floatLeft != -1 && floatRight != -1) {
				floatRight = floatLeft - floatRight;
			}	
		
			if (strLeft != "" && strRight != "") {
				throw new IllegalArgumentException();
			}
			
		}
	
		
}

	@Override
	public void visit(AndExpression arg0) {
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		
		leftExpression.accept(new Evaluator(schema,tuple));
		rightExpression.accept(new Evaluator(schema,tuple));
		
	}

	@Override
	public void visit(EqualsTo arg0) {
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		
		leftExpression.accept(new Evaluator(schema,tuple));
		rightExpression.accept(new Evaluator(schema,tuple));
		if (floatLeft != -1 && floatRight != -1) {
			if (floatLeft != floatRight)
				bool = false;
		}
		
		if (strLeft != "" && strRight != "") {
			if (strLeft.compareTo(strRight) != 0) 
				bool = false;
		}

	}

	@Override
	public void visit(GreaterThan arg0) {
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		
		leftExpression.accept(new Evaluator(schema,tuple,true,false));
		rightExpression.accept(new Evaluator(schema,tuple,false,true));
	
		if (floatLeft != -1 && floatRight != -1) {
			if (floatLeft <= floatRight)
				bool = false;
		}
		
		if (strLeft != "" && strRight != "") {
			if (strLeft.compareTo(strRight) == -1) 
				bool = false;
		}
		
		
	}

	@Override
	public void visit(GreaterThanEquals arg0) {
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		 
		leftExpression.accept(new Evaluator(schema,tuple));
		rightExpression.accept(new Evaluator(schema,tuple));
		
		if (floatLeft != -1 && floatRight != -1) {
			if (floatLeft < floatRight)
				bool = false;
		}
		
		if (strLeft != "" && strRight != "") {
			if (strLeft.compareTo(strRight) == 1) 
				bool = false;
		}

	}

	@Override
	public void visit(MinorThan arg0) {
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		
		leftExpression.accept(new Evaluator(schema,tuple));
		rightExpression.accept(new Evaluator(schema,tuple));
		if (floatLeft != -1 && floatRight != -1) {
			if (floatLeft >= floatRight)
				bool = false;
		}
		
		if (strLeft != "" && strRight != "") {
			if (strLeft.compareTo(strRight) > -1) 
				bool = false;
		}

	}

	@Override
	public void visit(MinorThanEquals arg0) {
		Expression leftExpression = arg0.getLeftExpression();
		Expression rightExpression = arg0.getRightExpression();
		
		leftExpression.accept(new Evaluator(schema,tuple));
		rightExpression.accept(new Evaluator(schema,tuple));
		
		if (floatLeft != -1 && floatRight != -1) {
			if (floatLeft > floatRight)
				bool = false;
		}
		
		if (strLeft != "" && strRight != "") {
			if (strLeft.compareTo(strRight) > 0) 
				bool = false;
		}

	}
	
	/*
	 *literals or Leaf Nodes of the recursion tree
	 */
	
	@Override
	public void visit(DoubleValue arg0) {
		FLOAT doubleVal = new FLOAT(arg0.toString());
		try {
		if (left) 
			floatLeft = doubleVal.toFLOAT();
		else
			floatRight = doubleVal.toFLOAT();
		}catch (Exception e) {e.printStackTrace();}
	}

	@Override
	public void visit(LongValue arg0) {
		FLOAT longVal = new FLOAT(arg0.toString());
		try {
		if (left) 
			floatLeft = longVal.toFLOAT();
		else if (right)
			floatRight = longVal.toFLOAT();
		}catch (Exception e) {e.printStackTrace();}
	}

	@Override
	public void visit(DateValue arg0) {
		Date date = arg0.getValue(); 
		STRING dateVal = new STRING(date.toString());
		if (left) 
			strLeft = dateVal.toSTRING();
		else if (right)
			strRight = dateVal.toSTRING();

	}

	@Override
	public void visit(StringValue arg0) {
		STRING stringVal = new STRING(arg0.toString());
		if (left) 
			strLeft = stringVal.toSTRING();
		else if (right)
			strRight = stringVal.toSTRING();

	}

	@Override
	public void visit(Column arg0) {
			
		String columnVal = arg0.getColumnName();
		Datum columnTupleVal = null;
		int count = 0;
		float floatVal = -1;
		String strVal = "";
		for (ColumnSchema col: schema) {
			if(col.colName.equals(columnVal)) {
				columnTupleVal = tuple[count];
				try {
					if(col.type == Datum.type.FLOAT)
						floatVal = columnTupleVal.toFLOAT();
					else if (col.type == Datum.type.STRING) 
						strVal = columnTupleVal.toSTRING();
				} catch (CastException e) {
					e.printStackTrace();
				}
				if (left) {
					if (floatVal != -1)
						floatLeft = floatVal;
					else if (strVal != "")
						strLeft = strVal;
				}
				else if (right) {
					if (floatVal != -1)
						floatRight = floatVal;
					else if (strVal != "")
						strRight = strVal;
				}
				break;
			}
			count++;
		}
	}
	
}
