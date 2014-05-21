package edu.buffalo.cse562.visitor;

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
    public void visit(Select select) {
        throw new UnsupportedOperationException(select.getClass().toString());
    }

    @Override
    public void visit(Delete delete) {
        throw new UnsupportedOperationException(delete.getClass().toString());
    }

    @Override
    public void visit(Update update) {
        throw new UnsupportedOperationException(update.getClass().toString());
    }

    @Override
    public void visit(Insert insert) {
        throw new UnsupportedOperationException(insert.getClass().toString());
    }

    @Override
    public void visit(Replace replace) {
        throw new UnsupportedOperationException(replace.getClass().toString());
    }

    @Override
    public void visit(Drop drop) {
        throw new UnsupportedOperationException(drop.getClass().toString());
    }

    @Override
    public void visit(Truncate truncate) {
        throw new UnsupportedOperationException(truncate.getClass().toString());
    }

    @Override
    public void visit(CreateTable createTable) {
        throw new UnsupportedOperationException(createTable.getClass().toString());
    }
}
