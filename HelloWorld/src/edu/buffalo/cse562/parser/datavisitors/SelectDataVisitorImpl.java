/*
 * @author: Dev Bharadwaj
 * Description: Operator of Tree is created per PlainSelect statement
 * Even Union is List<PlainSelect> so the output from different PlainSelects can be merged.
 * 
 * Data: Subhendu currently working on the in-memory DB representation.
 * The datagrabber implementation will have to change. To work for in-memory DB.
 */

package edu.buffalo.cse562.parser.datavisitors;

import edu.buffalo.cse562.datagrabber.DataGrabber;
import edu.buffalo.cse562.parser.defaultimpl.AbstractSelectVisitor;
import edu.buffalo.cse562.queryparser.TreeMaker;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.List;

public class SelectDataVisitorImpl extends AbstractSelectVisitor {
    private String result;
    private DataGrabber dataGrabber;

    public SelectDataVisitorImpl(DataGrabber dataGrabber) {
        this.dataGrabber = dataGrabber;
    }

    public String getResult() {
        return result;
    }

    @Override
    public void visit(PlainSelect plainSelect) {
    	
    	TreeMaker operatorTree = new TreeMaker(plainSelect);
 
    	/* DataGrabber implementation to be changed */
    	
//    	
//        List<SelectItem> selectItems = plainSelect.getSelectItems();
//        FromItem fromItem = plainSelect.getFromItem();
//        Expression whereCondition = plainSelect.getWhere();
//        result = dataGrabber.retrieveItemsFrom(selectItems, fromItem, whereCondition);
//    
    }

}
