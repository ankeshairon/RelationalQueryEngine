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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.buffalo.cse562.schema.SchemaUtils.createSchemaFromTableInfo;

public class TupleUpdater {

    private final IndexService indexService;
    private TableInfo tableInfo;
    private List<Long> rowIds;

    public TupleUpdater(TableInfo tableInfo, Expression where) {
        this.tableInfo = tableInfo;
        indexService = IndexService.getInstance();

        ColumnSchema[] tableSchema = createSchemaFromTableInfo(tableInfo);
        Integer columnPosition = SchemaUtils.getColumnIndexInSchema(tableSchema, ExpressionUtils.getColumnName(where));
        IndexedDataMap indexedDataMap = indexService.getIndexedDataFor(tableInfo.getTableName(), tableSchema, columnPosition);
        rowIds = indexedDataMap.getRowIdsForCondition(where);
    }

    public void removeTuples() {
        indexService.deleteTuplesFromTable(tableInfo, rowIds);
    }

    public void updateValueOfToForTuples(Column column, Expression expression) {
        final Integer columnPosition = SchemaUtils.getColumnIndexInColDefn(tableInfo.getColumnDefinitions(), column.getColumnName());

        final StringValueExtractor stringValueExtractor = new StringValueExtractor();
        expression.accept(stringValueExtractor);
        String newValue = stringValueExtractor.getValue();

        Map<Long, String> updatedTuples = getTuplesToBeReplacedForAllTuplesCellAtPositionWith(columnPosition, newValue);
        indexService.updateTuples(tableInfo, updatedTuples);

    }

    private Map<Long, String> getTuplesToBeReplacedForAllTuplesCellAtPositionWith(Integer columnPosition, String newValue) {
        final PrimaryStoreMap<Long, String> primaryStoreMap = indexService.getPrimaryStoreMap(tableInfo.getTableName());
        Map<Long, String> updatedTuples = new HashMap<>();

        String[] cells;
        String updatedTuple;

        for (Long rowId : rowIds) {
            cells = getUpdateCellValuesForTupleWithId(columnPosition, newValue, primaryStoreMap, rowId);
            updatedTuple = createUpdatedTuple(cells);
            updatedTuples.put(rowId, updatedTuple);
        }
        return updatedTuples;
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
