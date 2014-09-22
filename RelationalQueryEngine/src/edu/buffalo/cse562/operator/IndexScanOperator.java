package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.model.TableInfo;
import edu.buffalo.cse562.operator.abstractoperators.FilterOperator;
import edu.buffalo.cse562.operator.utils.indexscan.IndexScanHelper;
import edu.buffalo.cse562.schema.ColumnSchema;
import jdbm.PrimaryStoreMap;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

import java.util.List;
import java.util.ListIterator;

import static edu.buffalo.cse562.operator.utils.scan.ScanUtils.getDataForRelevantColumnIndexes;
import static edu.buffalo.cse562.schema.SchemaUtils.createSchemaFromTableInfo;

public class IndexScanOperator implements FilterOperator {
    private ColumnSchema[] schema;
    private List<Integer> relevantColumnIndexes;

    private PrimaryStoreMap<Long, String> storeMap;
    private ListIterator<Long> keyListIterator;
    private List<Long> keyList;

    private List<Expression> conditions;

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
        return getDataForRelevantColumnIndexes(keyListIterator.next(), relevantColumnIndexes, storeMap, schema);
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
        this.conditions = conditions;
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

    @Override
    public List<Expression> getConditions() {
        return conditions;
    }

    @Override
    public List<Integer> getRelevantColumnIndexes() {
        return relevantColumnIndexes;
    }
}
