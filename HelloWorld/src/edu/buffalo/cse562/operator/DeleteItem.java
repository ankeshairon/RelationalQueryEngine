package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.indexer.service.IndexService;
import edu.buffalo.cse562.indexer.service.IndexedDataMap;
import edu.buffalo.cse562.model.TableInfo;
import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.schema.SchemaUtils;
import net.sf.jsqlparser.statement.delete.Delete;

import java.util.List;

public class DeleteItem {


    private Delete deleteStatement;
    private TableInfo tableInfo;

    public DeleteItem(Delete deleteStatement, TableInfo tabInfo) {
        this.deleteStatement = deleteStatement;
        this.tableInfo = tabInfo;
    }

    public void remove() {
        ColumnSchema[] colSchema = SchemaUtils.createSchemaFromTableInfo(tableInfo);
        Integer columnPosition = SchemaUtils.getColumnIndexInColSchema(colSchema, "ORDERKEY");
        IndexService indexMap = IndexService.getInstance();
        IndexedDataMap indexData = indexMap.getIndexedDataFor(deleteStatement.getTable().toString(), colSchema, columnPosition);
        List<Long> rowID = indexData.getRowIdsForCondition(deleteStatement.getWhere());
        indexMap.deleteTuplesFromTable(deleteStatement.getTable().toString(), rowID);
    }
}
