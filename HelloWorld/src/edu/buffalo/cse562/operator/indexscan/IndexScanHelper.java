package edu.buffalo.cse562.operator.indexscan;

import edu.buffalo.cse562.indexer.service.IndexService;
import edu.buffalo.cse562.indexer.service.IndexedDataMap;
import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.schema.SchemaUtils;
import jdbm.PrimaryStoreMap;
import net.sf.jsqlparser.expression.Expression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static edu.buffalo.cse562.utils.ExpressionUtils.getColumnName;

public class IndexScanHelper {
    private final List<Expression> conditions;
    private final ColumnSchema[] schema;
    private IndexService indexService;
    private List<Long> filteredRowIds;

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
            filteredRowIds = new ArrayList<>();
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
            final List<Long> allTupleIds = indexService.getAllTupleIds(schema[0].getTblName());
            rowIdsForAllConditions.add(allTupleIds);
        }
        return rowIdsForAllConditions;
    }

    private List<Long> getRowIdsForCondition(Expression condition) {
        IndexedDataMap indexedDataMap;

        indexedDataMap = indexService.getIndexedDataFor(
                schema[0].getTblName(),
                schema,
                SchemaUtils.getColumnIndexInSchema(schema, getColumnName(condition))
        );

        return indexedDataMap.getRowIdsForCondition(condition);
    }

    public List<Long> getFilteredRowIds() {
        return filteredRowIds;
    }

    public PrimaryStoreMap<Long, String> getStoreMap() {
        return indexService.getPrimaryStoreMap(schema[0].getTblName());
    }
}
