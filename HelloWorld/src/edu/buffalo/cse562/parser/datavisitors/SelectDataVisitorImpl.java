package edu.buffalo.cse562.parser.datavisitors;

import edu.buffalo.cse562.parser.defaultimpl.AbstractSelectVisitor;
import edu.buffalo.cse562.processor.TreeMaker;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class SelectDataVisitorImpl extends AbstractSelectVisitor {
    private TreeMaker treeMaker;

    public SelectDataVisitorImpl(TreeMaker treeMaker) {
        this.treeMaker = treeMaker;
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        treeMaker.execute(plainSelect);
    }

}
