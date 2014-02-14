package edu.buffalo.cse562.parser.defaultimpl;

import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

public class AbstractStatementVisitor implements StatementVisitor {


    @Override
    public void visit(Delete arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void visit(Update arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void visit(Insert arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void visit(Replace arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void visit(Drop arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void visit(Truncate arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void visit(Select arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void visit(CreateTable arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
