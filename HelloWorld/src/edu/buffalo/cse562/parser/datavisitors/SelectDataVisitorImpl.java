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
import net.sf.jsqlparser.statement.select.PlainSelect;

public class SelectDataVisitorImpl extends AbstractSelectVisitor {
    private String result;
    private DataGrabber dataGrabber;
    private TreeMaker operatorStack;

    public SelectDataVisitorImpl(DataGrabber dataGrabber, TreeMaker operatorStack) {
        this.dataGrabber = dataGrabber;
        this.operatorStack = operatorStack;
    }

    public String getResult() {
        return result;
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        operatorStack.makeTree(plainSelect);
 
    	/* DataGrabber implementation to be changed */

//    	
//        List<SelectItem> selectItems = plainSelect.getSelectItems();
//        FromItem fromItem = plainSelect.getFromItem();
//        Expression whereCondition = plainSelect.getWhere();
//        result = dataGrabber.retrieveItemsFrom(selectItems, fromItem, whereCondition);
//    
    }

}
