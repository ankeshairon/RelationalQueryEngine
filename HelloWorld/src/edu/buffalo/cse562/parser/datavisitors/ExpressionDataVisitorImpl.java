package edu.buffalo.cse562.parser.datavisitors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import edu.buffalo.cse562.parser.defaultimpl.AbstractExpressionVisitor;
import edu.buffalo.cse562.processor.ExpressionTree;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.schema.Column;

public class ExpressionDataVisitorImpl extends AbstractExpressionVisitor {

	ExpressionTree expressionTree;
	
	public ExpressionDataVisitorImpl() {
		expressionTree = new ExpressionTree();
	}
	public ExpressionDataVisitorImpl(ExpressionTree exprTree) {
		expressionTree = exprTree;		
	}
	
	public void showTree() {
		expressionTree.traverse();
	}
	
	public ExpressionTree getExpressionTree() {
		return expressionTree;
	}
	
	private void addToExpressionTree(Expression leftExpression, Expression rightExpression) {
		if (leftExpression instanceof BinaryExpression) {
        	expressionTree.moveLeft();
        	leftExpression.accept(new ExpressionDataVisitorImpl(expressionTree));
        }
        else 
        	expressionTree.insert(leftExpression);
        
        if (rightExpression instanceof BinaryExpression) {
        	expressionTree.moveRight();
        	rightExpression.accept(new ExpressionDataVisitorImpl(expressionTree));
        }
        else
        	expressionTree.insert(rightExpression);

	}
	
	@Override
	public void visit(MinorThanEquals expr) {
		System.out.println("And MinorThanEquals: " + expr);
	  	Expression leftExpression = expr.getLeftExpression();
        Expression rightExpression = expr.getRightExpression();
        
        expressionTree.insert(expr);
        addToExpressionTree(leftExpression, rightExpression);
        
     }
	@Override
	public void visit(MinorThan expr) {
		System.out.println("And MinorThan: " + expr);
	  	Expression leftExpression = expr.getLeftExpression();
        Expression rightExpression = expr.getRightExpression();
        
        expressionTree.insert(expr);
        addToExpressionTree(leftExpression, rightExpression);
	}
	
	
	
    @Override
    public void visit(GreaterThanEquals expr) {
		System.out.println("And GreaterThanEquals: " + expr);
	  	Expression leftExpression = expr.getLeftExpression();
        Expression rightExpression = expr.getRightExpression();
        
        expressionTree.insert(expr);
        addToExpressionTree(leftExpression, rightExpression);
    }

    @Override
    public void visit(GreaterThan expr) {
    	System.out.println("And GreaterThan: " + expr);
	  	Expression leftExpression = expr.getLeftExpression();
        Expression rightExpression = expr.getRightExpression();
        
        expressionTree.insert(expr);
        addToExpressionTree(leftExpression, rightExpression);
    }

    @Override
    public void visit(EqualsTo expr) {
    	System.out.println("And EqualsTo: " + expr);
	  	Expression leftExpression = expr.getLeftExpression();
        Expression rightExpression = expr.getRightExpression();
        
        expressionTree.insert(expr);
        addToExpressionTree(leftExpression, rightExpression);
    }

    @Override
    public void visit(AndExpression expr) {
    	System.out.println("And Expr: " + expr);
	  	Expression leftExpression = expr.getLeftExpression();
        Expression rightExpression = expr.getRightExpression();
        
        expressionTree.insert(expr);
        addToExpressionTree(leftExpression, rightExpression);
    }

    @Override
    public void visit(OrExpression expr) {
        System.out.println("And ORExpr: " + expr);
	  	Expression leftExpression = expr.getLeftExpression();
        Expression rightExpression = expr.getRightExpression();
        
        expressionTree.insert(expr);
        addToExpressionTree(leftExpression, rightExpression);
    }

    @Override
    public void visit(Multiplication expr) {
    	System.out.println("And Multiplication: " + expr);
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
    public void visit(Subtraction expr) {
    	System.out.println("And Subtraction: " + expr);
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
    public void visit(Addition expr) {
    	System.out.println("And Addition: " + expr);
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
    public void visit(Division expr) {
    	System.out.println("And Division: " + expr);
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
    public void visit(BitwiseAnd expr) {
	  	Expression leftExpression = expr.getLeftExpression();
        Expression rightExpression = expr.getRightExpression();
        
        expressionTree.insert(expr);
        addToExpressionTree(leftExpression, rightExpression);
    }

    @Override
    public void visit(BitwiseOr expr) {
	  	Expression leftExpression = expr.getLeftExpression();
        Expression rightExpression = expr.getRightExpression();
        
        expressionTree.insert(expr);
        addToExpressionTree(leftExpression, rightExpression);
    }
}
