package edu.buffalo.cse562.parser.datavisitors;

import edu.buffalo.cse562.parser.defaultimpl.AbstractSelectVisitor;
import edu.buffalo.cse562.processor.DataProcessor;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class SelectDataVisitorImpl extends AbstractSelectVisitor {
    private DataProcessor treeMaker;

    public SelectDataVisitorImpl(DataProcessor treeMaker) {
        this.treeMaker = treeMaker;
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        treeMaker.execute(plainSelect);
    }

}
