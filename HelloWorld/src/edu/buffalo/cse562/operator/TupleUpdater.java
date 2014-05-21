package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.indexer.service.IndexService;
import edu.buffalo.cse562.indexer.service.IndexedDataMap;
import edu.buffalo.cse562.model.TableInfo;
import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.schema.SchemaUtils;
import edu.buffalo.cse562.utils.ExpressionUtils;
import edu.buffalo.cse562.visitor.StringValueExtractor;
import jdbm.PrimaryStoreMap;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

import java.util.Iterator;
import java.util.List;

import static edu.buffalo.cse562.schema.SchemaUtils.createSchemaFromTableInfo;

public class TupleUpdater {

    private TableInfo tableInfo;
    private List<Long> rowIds;

    public TupleUpdater(TableInfo tableInfo, Expression where) {
        this.tableInfo = tableInfo;
        ColumnSchema[] tableSchema = createSchemaFromTableInfo(tableInfo);
        Integer columnPosition = SchemaUtils.getColumnIndexInColSchema(tableSchema, ExpressionUtils.getColumnName(where));
        IndexedDataMap indexedDataMap = IndexService.getInstance().getIndexedDataFor(tableInfo.getTableName(), tableSchema, columnPosition);
        rowIds = indexedDataMap.getRowIdsForCondition(where);
    }

    public void removeTuples() {
        IndexService.getInstance().deleteTuplesFromTable(tableInfo.getTableName(), rowIds);
    }

    public void updateValueOfToForTuples(List<Column> columns, List<Expression> expressions) {
        final Iterator<Column> columnIterator = columns.iterator();
        final Iterator<Expression> expressionIterator = expressions.iterator();
        Expression expression;
        Column column;

        while (columnIterator.hasNext()) {
            column = columnIterator.next();
            expression = expressionIterator.next();

            final Integer columnPosition = SchemaUtils.getColumnIndexInColDefn(tableInfo.getColumnDefinitions(), column.getColumnName());

            final StringValueExtractor stringValueExtractor = new StringValueExtractor();
            expression.accept(stringValueExtractor);
            String newValue = stringValueExtractor.getValue();

            replaceForAllTuplesCellAtPositionWith(columnPosition, newValue);
        }

    }

    private void replaceForAllTuplesCellAtPositionWith(Integer columnPosition, String newValue) {
        final PrimaryStoreMap<Long, String> primaryStoreMap = IndexService.getInstance().getPrimaryStoreMap(tableInfo.getTableName());
        String[] cells;
        String updatedTuple;

        for (Long rowId : rowIds) {
            cells = getUpdateCellValuesForTupleWithId(columnPosition, newValue, primaryStoreMap, rowId);
            updatedTuple = createUpdatedTuple(cells);
            primaryStoreMap.put(rowId, updatedTuple);
        }
    }

    private String createUpdatedTuple(String[] cells) {
        StringBuilder tupleBuilder = new StringBuilder();
        for (String cell : cells) {
            tupleBuilder.append("|").append(cell);
        }
        return tupleBuilder.substring(1);
    }

    private String[] getUpdateCellValuesForTupleWithId(Integer columnPosition, String newValue, PrimaryStoreMap<Long, String> primaryStoreMap, Long rowId) {
        String tuple = primaryStoreMap.get(rowId);
        String[] cells = tuple.split("\\|");
        cells[columnPosition] = newValue;
        return cells;
    }
}
