package edu.buffalo.cse562.utils;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

public class ExpressionUtils {

    /**
     * To be used to extract column name from expression if the expression is known to have only one column reference
     */
    public static String getColumnName(Expression expression) {
        if (expression instanceof Column) {
            return ((Column) expression).getColumnName();
        } else if (expression instanceof BinaryExpression) {
            final BinaryExpression binaryExpression = (BinaryExpression) expression;
            if (binaryExpression.getLeftExpression() instanceof Column) {
                return getColumnName(binaryExpression.getLeftExpression());
            } else {
                return getColumnName(binaryExpression.getRightExpression());
            }
        } else {
            throw new UnsupportedOperationException("Unable to find column name in expression");
        }
    }
}
