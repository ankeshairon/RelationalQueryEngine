package edu.buffalo.cse562.indexer.visitors;

import edu.buffalo.cse562.indexer.DataIndexer;
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

import java.io.File;
import java.io.IOException;
import java.util.List;

public class IndexingStatementVisitor implements StatementVisitor {

    private DataIndexer dataIndexer;


    public IndexingStatementVisitor(File dataDir, File indexDir) throws IOException {
        dataIndexer = new DataIndexer(dataDir, indexDir);
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
        TableIndexingInfo tableIndexingInfo = new TableIndexingInfo(createTable.getTable().getName(), createTable.getColumnDefinitions(), null);

        final List<Index> indexes = createTable.getIndexes();
        if (indexes == null) {
            return;
        }

        for (int i = 0; i < indexes.size(); i++) {
            tableIndexingInfo.addIndex(indexes.get(i), i);
        }

        dataIndexer.buildIndexes(tableIndexingInfo);
    }

    public void cleanUp() {
        dataIndexer.cleanUp();
    }
}
