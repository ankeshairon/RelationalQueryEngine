package edu.buffalo.cse562.parser.datavisitors;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.schema.Column;
import edu.buffalo.cse562.model.data.Tuple;
import edu.buffalo.cse562.parser.defaultimpl.AbstractExpressionVisitor;

public class ExpressionTreeExecutor extends AbstractExpressionVisitor {

	boolean toAdd = false;
	List<Tuple> workingSet;
	
	public ExpressionTreeExecutor(List<Tuple> workingSet) {
		this.workingSet = workingSet;
	}
	
	public boolean getToAdd() {
		return toAdd;
	}
	@Override
	public void visit(Function fnctn) {
		// TODO Auto-generated method stub
		super.visit(fnctn);
	}

	@Override
	public void visit(DoubleValue dv) {
		// TODO Auto-generated method stub
		super.visit(dv);
	}

	@Override
	public void visit(LongValue lv) {
		// TODO Auto-generated method stub
		super.visit(lv);
	}

	@Override
	public void visit(DateValue dv) {
		// TODO Auto-generated method stub
		super.visit(dv);
	}

	@Override
	public void visit(StringValue sv) {
		// TODO Auto-generated method stub
		super.visit(sv);
	}

	@Override
	public void visit(Addition expr) {
		Pattern pattern = Pattern.compile("[\\d()+*/.\\-\\s]*");
    	Matcher matcher = pattern.matcher(expr.toString().trim());
    	if (matcher.matches()) {
    		try {
    			ScriptEngineManager manager = new ScriptEngineManager();
    			ScriptEngine engine = manager.getEngineByName("JavaScript");
    			System.out.println("## " + engine.eval(expr.toString()));
    			String val = engine.eval(expr.toString()).toString();
    			if (val.contains(".")) {
    				DoubleValue doubleExpr = new DoubleValue(val);
    				expressionTree.insert(doubleExpr);
    			}
    			else {
    				LongValue longExpr = new LongValue(val);
    				expressionTree.insert(longExpr);
    			}
    		} catch(Exception e) {e.printStackTrace();}
    	}
    	else {
    	  	Expression leftExpression = expr.getLeftExpression();
            Expression rightExpression = expr.getRightExpression();
            
            expressionTree.insert(expr);
            addToExpressionTree(leftExpression, rightExpression);
    	}
	}

	@Override
	public void visit(Division dvsn) {
		// TODO Auto-generated method stub
		super.visit(dvsn);
	}

	@Override
	public void visit(Multiplication m) {
		// TODO Auto-generated method stub
		super.visit(m);
	}

	@Override
	public void visit(Subtraction s) {
		// TODO Auto-generated method stub
		super.visit(s);
	}

	@Override
	public void visit(AndExpression ae) {
		// TODO Auto-generated method stub
		super.visit(ae);
	}

	@Override
	public void visit(OrExpression oe) {
		// TODO Auto-generated method stub
		super.visit(oe);
	}

	@Override
	public void visit(EqualsTo et) {
		// TODO Auto-generated method stub
		super.visit(et);
	}

	@Override
	public void visit(GreaterThan gt) {
		// TODO Auto-generated method stub
		super.visit(gt);
	}

	@Override
	public void visit(GreaterThanEquals gte) {
		// TODO Auto-generated method stub
		super.visit(gte);
	}

	@Override
	public void visit(MinorThan mt) {
		// TODO Auto-generated method stub
		super.visit(mt);
	}

	@Override
	public void visit(MinorThanEquals mte) {
		// TODO Auto-generated method stub
		super.visit(mte);
	}

	@Override
	public void visit(Column column) {
		// TODO Auto-generated method stub
		super.visit(column);
	}

}