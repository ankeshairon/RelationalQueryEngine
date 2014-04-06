package edu.buffalo.cse562.visitor;

import edu.buffalo.cse562.model.TableInfo;
import edu.buffalo.cse562.operator.Operator;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

import java.io.File;
import java.util.HashMap;

public class MyStatementVisitor implements StatementVisitor {

    private final File dataDir;
    private File swapDir;
    private final HashMap<String, TableInfo> tables;
    public Operator source;

    public MyStatementVisitor(File dataDir, File swapDir) {
        this.dataDir = dataDir;
        this.swapDir = swapDir;
        tables = new HashMap<>();
    }

    @Override
    public void visit(Select stmnt) {
        MySelectVisitor myVisitor = new MySelectVisitor(dataDir, swapDir, tables);
        stmnt.getSelectBody().accept(myVisitor);
        source = myVisitor.source;
    }

    @Override
    public void visit(Delete arg0) {
        throw new UnsupportedOperationException("Delete not supported");
    }

    @Override
    public void visit(Update arg0) {
        throw new UnsupportedOperationException("Update not supported");
    }

    @Override
    public void visit(Insert arg0) {
        throw new UnsupportedOperationException("Insert not supported");
    }

    @Override
    public void visit(Replace arg0) {
        throw new UnsupportedOperationException("Replace not supported");
    }

    @Override
    public void visit(Drop arg0) {
        throw new UnsupportedOperationException("Drop not supported");
    }

    @Override
    public void visit(Truncate arg0) {
        throw new UnsupportedOperationException("Truncate not supported");
    }

    @Override
    public void visit(CreateTable stmnt) {
        final String tableName = stmnt.getTable().getName().toLowerCase();
        tables.put(tableName, new TableInfo(tableName, stmnt.getColumnDefinitions(), getFileSize(tableName)));
    }

    private Long getFileSize(String tableName) {
        return new File(dataDir.getAbsolutePath() + "//" + tableName + ".dat").length();
    }

}
