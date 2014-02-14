package edu.buffalo.cse562.parser.defaultimpl;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.Union;

public class AbstractSelectVisitor implements SelectVisitor {

    @Override
    public void visit(PlainSelect plainSelect) {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public void visit(Union union) {
        throw new UnsupportedOperationException("Not supported yet");
    }
}
