package edu.buffalo.cse562.operator.indexscan;

import edu.buffalo.cse562.indexer.service.IndexService;
import edu.buffalo.cse562.indexer.service.IndexedDataMap;
import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.schema.SchemaUtils;
import jdbm.PrimaryStoreMap;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IndexScanHelper {
    private IndexService indexService;
    private List<Long> filteredRowIds;

    private final List<Expression> conditions;
    private final ColumnSchema[] schema;

    public IndexScanHelper(ColumnSchema[] schema, List<Expression> conditions) {
        indexService = IndexService.getInstance();
        this.schema = schema;
        this.conditions = conditions;
        evaluate();
    }

    private void evaluate() {
        final List<List<Long>> rowIdsForAllConditions = getRowIdsForAllConditions();
        final Iterator<List<Long>> iterator = rowIdsForAllConditions.iterator();

        if (iterator.hasNext()) {
            filteredRowIds = iterator.next();
        } else {
            return;
        }

        while (iterator.hasNext()) {
            filteredRowIds.retainAll(iterator.next());
        }
    }

    private List<List<Long>> getRowIdsForAllConditions() {
        List<List<Long>> rowIdsForAllConditions = new ArrayList<>();
        List<Long> rowIdsForCondition;

        if (conditions != null) {
            for (Expression condition : conditions) {
                rowIdsForCondition = getRowIdsForCondition(condition);
                rowIdsForAllConditions.add(rowIdsForCondition);
            }
        } else {
            rowIdsForAllConditions.add(indexService.getAllTupleIds(schema[0].getTblName()));
        }
        return rowIdsForAllConditions;
    }

    private List<Long> getRowIdsForCondition(Expression condition) {
        IndexedDataMap indexedDataMap;

        indexedDataMap = indexService.getIndexedDataFor(
                schema[0].getTblName(),
                schema,
                SchemaUtils.getColumnIndexInColSchema(schema, getColumnName(condition))
        );

        return indexedDataMap.getRowIdsWhereColumnIsXThanValue(condition);
    }


    private String getColumnName(Expression expression) {
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

    public List<Long> getFilteredRowIds() {
        return filteredRowIds;
    }


    public PrimaryStoreMap<Long, String> getStoreMap() {
        return indexService.getPrimaryStoreMap(schema[0].getTblName());
    }
}
