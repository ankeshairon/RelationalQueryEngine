package edu.buffalo.cse562.visitor;

import edu.buffalo.cse562.model.TableInfo;
import edu.buffalo.cse562.operator.abstractoperators.Operator;
import edu.buffalo.cse562.visitor.optimizer.ScanOptimizer;
import edu.buffalo.cse562.visitor.utils.TupleUpdater;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

import java.io.File;
import java.util.HashMap;

public class MyStatementVisitor extends AbstractStatementVisitor {

    private final HashMap<String, TableInfo> tablesInfo;
    private final File dataDir;
    public Operator source;
    private File swapDir;

    private BatchInsertVisitor batchInsertVisitor;

    public MyStatementVisitor(File dataDir, File swapDir) {
        this.dataDir = dataDir;
        this.swapDir = swapDir;
        tablesInfo = new HashMap<>();
        batchInsertVisitor = new BatchInsertVisitor(tablesInfo);
    }

    @Override
    public void visit(Select statement) {
        batchInsertVisitor.finishPendingOperations();
        if (tablesInfo.size() != 0) {
            new ScanOptimizer(tablesInfo, statement).populateRelevantColumnIndexes();
        } else {
            throw new UnsupportedOperationException("Missing create table statements");
        }

        MySelectVisitor myVisitor = new MySelectVisitor(dataDir, swapDir, tablesInfo);
        statement.getSelectBody().accept(myVisitor);
        source = myVisitor.source;
    }

    @Override
    public void visit(Delete arg0) {
        final String tableName = arg0.getTable().toString().toLowerCase();
        TupleUpdater tupleUpdater = new TupleUpdater(tablesInfo.get(tableName), arg0.getWhere());
        tupleUpdater.removeTuples();
    }

    @Override
    public void visit(Update arg0) {
        final String tableName = arg0.getTable().toString().toLowerCase();
        TupleUpdater tupleUpdater = new TupleUpdater(tablesInfo.get(tableName), arg0.getWhere());
        tupleUpdater.updateValueOfToForTuples((Column) arg0.getColumns().get(0), (Expression) arg0.getExpressions().get(0));
    }

    @Override
    public void visit(Insert insert) {
        batchInsertVisitor.addInsertItem(insert);
    }

    @Override
    public void visit(CreateTable stmnt) {
        final Table table = stmnt.getTable();
        final String alias = table.getAlias();
        final String tableName = table.getName();
        final TableInfo tableInfo = new TableInfo(tableName, stmnt.getColumnDefinitions(), getFileSize(tableName));

        tablesInfo.put(tableName.toLowerCase(), tableInfo);
        if (alias != null) {
            tablesInfo.put(alias, tableInfo);
        }
    }

    private Long getFileSize(String tableName) {
        return new File(dataDir.getAbsolutePath() + "//" + tableName + ".dat").length();
    }

}
