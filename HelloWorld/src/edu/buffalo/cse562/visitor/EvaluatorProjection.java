package edu.buffalo.cse562.visitor;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.schema.Column;

import java.util.List;

public class EvaluatorProjection extends AbstractExpressionVisitor {

    static ColumnSchema[] inputSchema;
    static List<ColumnSchema> outputSchema;
    static List<Integer> indexes;
	Expression expression;
	String alias = null;
	static int counter = 0;
    private boolean isAnAggregation;

    public EvaluatorProjection(ColumnSchema[] inputSchemaArg, List<ColumnSchema> outputSchemaArg, List<Integer> indexesArg) {
        inputSchema = inputSchemaArg;
		outputSchema = outputSchemaArg;
		indexes = indexesArg;
	}
	
	public EvaluatorProjection(Expression expression, String alias) {
		this.expression = expression;
		this.alias = alias;
	}

    @Override
	public void visit(Function arg0) {
		/*String aggregate = arg0.getName();
		ExpressionList expressionList = arg0.getParameters();
		boolean isAllColumns = arg0.isAllColumns();
		boolean isDistinct = arg0.isDistinct();
		
		 * Aggregates don't follow the pull model 
		 * We need to implement the is_done function()
		 * This is to be a blocking method.
		 
		System.out.println(aggregate + ", All columns " + isAllColumns + ", Distinct " + isDistinct);
		List<Expression> expr = expressionList.getExpressions();
		Expression func = arg0;
		*/
        isAnAggregation = true;

        indexes.add(-1);
		String colName = null;
		if (alias == null) 
			colName = arg0.toString();
		else 
			colName = alias;
        ColumnSchema columnSchema = new ColumnSchema(colName, Datum.type.FLOAT);
        columnSchema.setAlias(alias);
        columnSchema.setExpression(arg0);
        outputSchema.add(columnSchema);
		counter++;
	}

	@Override
    public void visit(Parenthesis parenthesis) {
        parenthesis.getExpression().accept(this);
    }

	@Override
	public void visit(Column arg0) {
        isAnAggregation = false;

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

    @Override
    public void visit(Addition arg0) {
        visitBinaryExpression(arg0);
    }

    @Override
    public void visit(Division arg0) {
        visitBinaryExpression(arg0);
    }

    @Override
    public void visit(Multiplication arg0) {
        visitBinaryExpression(arg0);
    }

    @Override
    public void visit(Subtraction arg0) {
        visitBinaryExpression(arg0);
    }

    private void visitBinaryExpression(BinaryExpression binaryExpression) {
        //todo dev
    }

    public boolean isAnAggregation() {
        return isAnAggregation;
    }
}