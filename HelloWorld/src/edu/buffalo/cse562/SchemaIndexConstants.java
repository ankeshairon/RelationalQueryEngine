package edu.buffalo.cse562;

public class SchemaIndexConstants {
    public final static Integer SCHEMA_INDEX_INDICATING_EXPRESSION = -1;
    public final static Integer SCHEMA_INDEX_INDICATING_EXPRESSION_INSIDE_FUNCTION = -2;
    public final static Integer SCHEMA_INDEX_INDICATING_STAR_INSIDE_FUNCTION = -3;
    private final static Integer SCHEMA_INDEX_INDICATING_COLUMN_INSIDE_FUNCTION = -100;

    public static boolean isFunctionWithoutExpression(int schemaIndex) {
        return schemaIndex <= SCHEMA_INDEX_INDICATING_COLUMN_INSIDE_FUNCTION;
    }

    public static int getOldIndexReferencedByFunction(int schemaIndex) {
        return SCHEMA_INDEX_INDICATING_COLUMN_INSIDE_FUNCTION - schemaIndex;
    }

    public static boolean isExpressionInsideFunction(int schemaIndex) {
        return (schemaIndex == SCHEMA_INDEX_INDICATING_EXPRESSION_INSIDE_FUNCTION) ||
                isFunctionWithoutExpression(schemaIndex);
    }

    public static boolean isStarInsideFunction(int schemaIndex) {
        return schemaIndex == SCHEMA_INDEX_INDICATING_STAR_INSIDE_FUNCTION;
    }
}
