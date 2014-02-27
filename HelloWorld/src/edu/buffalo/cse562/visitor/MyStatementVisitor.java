package edu.buffalo.cse562.visitor;

import edu.buffalo.cse562.operator.Operator;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

import java.util.HashMap;
import java.util.List;

public class MyStatementVisitor implements StatementVisitor {

    public String dataDir;
    public HashMap<String, List<ColumnDefinition>> tables;
    public Operator source;

    public MyStatementVisitor(String dataDir) {
        this.dataDir = dataDir;
        tables = new HashMap<>();
    }

    @Override
    public void visit(Select stmnt) {
        MySelectVisitor myVisitor = new MySelectVisitor(dataDir, tables);
        SelectBody selectBody = stmnt.getSelectBody();
        selectBody.accept(myVisitor);
        source = myVisitor.source;
    }

    @Override
    public void visit(Delete arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Update arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Insert arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Replace arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Drop arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Truncate arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(CreateTable stmnt) {
        tables.put(stmnt.getTable().getName().toLowerCase(), stmnt.getColumnDefinitions());
    }

}
