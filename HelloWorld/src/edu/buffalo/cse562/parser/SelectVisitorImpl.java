package edu.buffalo.cse562.parser;

import edu.buffalo.cse562.datagrabber.DataGrabber;
import edu.buffalo.cse562.parser.defaultimpl.AbstractSelectVisitor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.List;

public class SelectVisitorImpl extends AbstractSelectVisitor {
    private String result;
    private DataGrabber dataGrabber;

    public SelectVisitorImpl(DataGrabber dataGrabber) {
        this.dataGrabber = dataGrabber;
    }

    public String getResult() {
        return result;
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        List<SelectItem> selectItems = plainSelect.getSelectItems();
        FromItem fromItem = plainSelect.getFromItem();
        Expression whereCondition = plainSelect.getWhere();
        result = dataGrabber.retrieveItemsFrom(selectItems, fromItem, whereCondition);
    }

}
