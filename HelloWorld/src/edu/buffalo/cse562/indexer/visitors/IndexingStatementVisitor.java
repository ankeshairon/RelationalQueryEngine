package edu.buffalo.cse562.indexer.visitors;

import edu.buffalo.cse562.indexer.model.TableIndexingInfo;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.Index;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.buffalo.cse562.schema.SchemaUtils.getColumnIndexIn;

public class IndexingStatementVisitor implements StatementVisitor {

    //tableName, random info
    Map<String, TableIndexingInfo> tablesIndexingInfo;

    public IndexingStatementVisitor() {
        tablesIndexingInfo = new HashMap<>();
    }

    @Override
    public void visit(Select select) {

    }

    @Override
    public void visit(Delete delete) {

    }

    @Override
    public void visit(Update update) {

    }

    @Override
    public void visit(Insert insert) {

    }

    @Override
    public void visit(Replace replace) {

    }

    @Override
    public void visit(Drop drop) {

    }

    @Override
    public void visit(Truncate truncate) {

    }

    @Override
    public void visit(CreateTable createTable) {
        final TableIndexingInfo tableIndexingInfo = new TableIndexingInfo();

        final List<Index> indexedColumns = createTable.getIndexes();
        for (Index indexedColumn : indexedColumns) {
            tableIndexingInfo.addIndexColumn(indexedColumn.getType(),
                                                                    indexedColumn.getName(),
                                                                    getColumnIndexIn(createTable.getColumnDefinitions(),
                                                                    indexedColumn.getName())
                                                                   );
        }
        tablesIndexingInfo.put(createTable.getTable().getName().toLowerCase(), tableIndexingInfo);
    }

}
