package edu.buffalo.cse562.operator;

import java.io.File;
import java.util.List;

import edu.buffalo.cse562.indexer.service.IndexService;
import edu.buffalo.cse562.indexer.service.IndexedDataMap;
import edu.buffalo.cse562.model.TableInfo;
import edu.buffalo.cse562.operator.indexscan.IndexScanOperator;
import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.schema.SchemaUtils;
import net.sf.jsqlparser.statement.delete.Delete;

public class DeleteItem {
	
	
	private Delete deleteStatement;
	private TableInfo tableInfo;
	private File indexDir;

	public DeleteItem(Delete deleteStatement, TableInfo tabInfo, File indexDir) {
		this.deleteStatement = deleteStatement;
		this.tableInfo = tabInfo;
		this.indexDir = indexDir;
	}
	
	public void remove() {
		IndexScanOperator dummyScan = new IndexScanOperator(tableInfo, null);
		ColumnSchema[] colSchema = dummyScan.getSchema();
		Integer columnPosition = SchemaUtils.getColumnIndexInColSchema(colSchema, "ORDERKEY");
		IndexService indexMap = new IndexService(indexDir); 
		IndexedDataMap indexData = indexMap.getIndexedDataFor(deleteStatement.getTable().toString(),colSchema,columnPosition);
		List<Long> rowID = indexData.getRowIdsWhereColumnIsXThanValue(deleteStatement.getWhere());
		indexMap.deleteTuplesFromTable(deleteStatement.getTable().toString(), rowID);
	}
}
