package edu.buffalo.cse562.parser.datavisitors;

import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
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
import edu.buffalo.cse562.parser.defaultimpl.AbstractExpressionVisitor;

public class ExpressionTreeExecutor extends AbstractExpressionVisitor {

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
	public void visit(Addition adtn) {
		// TODO Auto-generated method stub
		super.visit(adtn);
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