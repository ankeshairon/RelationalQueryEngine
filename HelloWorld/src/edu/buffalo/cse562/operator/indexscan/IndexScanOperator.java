package edu.buffalo.cse562.operator.indexscan;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.model.TableInfo;
import edu.buffalo.cse562.operator.Operator;
import edu.buffalo.cse562.schema.ColumnSchema;
import jdbm.PrimaryStoreMap;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

import java.util.List;
import java.util.ListIterator;

import static edu.buffalo.cse562.operator.utils.ScanUtils.getDatumsForAllColumnPositions;
import static edu.buffalo.cse562.operator.utils.ScanUtils.getDatumsForRelevantColumnPositions;
import static edu.buffalo.cse562.schema.SchemaUtils.createSchemaFromTableInfo;

public class IndexScanOperator implements Operator {

    private ColumnSchema[] schema;
    private List<Integer> relevantColumnIndexes;

    private PrimaryStoreMap<Long, String> storeMap;
    private ListIterator<Long> keyListIterator;
    private List<Long> keyList;

    public IndexScanOperator(TableInfo tableInfo, ColumnSchema[] finalSchema) {
        if (finalSchema == null) {
            makeSchema(tableInfo);
        } else {
            schema = finalSchema;
        }
        setConditionsToFilterDataOn(null);
    }

    @Override
    public Datum[] readOneTuple() {
        if (!keyListIterator.hasNext()) {
            return null;
        }

        Long rowId = keyListIterator.next();
        while (!storeMap.containsKey(rowId)) {
            if (keyListIterator.hasNext()) {
                rowId = keyListIterator.next();
            }
        }

        if (relevantColumnIndexes != null) {
            return getDatumsForRelevantColumnPositions(
                    storeMap.get(rowId),
                    relevantColumnIndexes,
                    schema);

        } else {
            return getDatumsForAllColumnPositions(
                    storeMap.get(rowId),
                    schema);
        }
    }

    @Override
    public void reset() {
        keyListIterator = keyList.listIterator();
    }

    @Override
    public ColumnSchema[] getSchema() {
        return schema;
    }

    @Override
    public Long getProbableTableSize() {
        return (long) (keyList.size() * 100);
    }

    public void setConditionsToFilterDataOn(List<Expression> conditions) {
        final IndexScanHelper helper = new IndexScanHelper(schema, conditions);
        keyList = helper.getFilteredRowIds();
        storeMap = helper.getStoreMap();
        reset();
    }

    private void makeSchema(TableInfo tableInfo) {
        ColumnDefinition columnDefinition;

        if ((relevantColumnIndexes = tableInfo.getColumnIndexesUsed()) != null) {
            final List<ColumnDefinition> allColumnDefinitions = tableInfo.getColumnDefinitions();
            schema = new ColumnSchema[relevantColumnIndexes.size()];

            for (int i = 0; i < relevantColumnIndexes.size(); i++) {
                columnDefinition = allColumnDefinitions.get(relevantColumnIndexes.get(i));
                schema[i] = new ColumnSchema(columnDefinition.getColumnName(), columnDefinition.getColDataType().getDataType());
                schema[i].setTableName(tableInfo.getTableName());
                schema[i].setTableAlias(tableInfo.getAlias());
            }
        } else {
            schema = createSchemaFromTableInfo(tableInfo);
        }
    }
}
