/*
 * @Author:  Dev Bharadwaj
 * Pass-in the PlainSelect object and create the logical list of operators
 * execute() is then called and data is passed in and out of the operators
 * We return the output back to PlainSelect.
 */

package edu.buffalo.cse562.queryparser;

import edu.buffalo.cse562.datagrabber.DataGrabber;
import edu.buffalo.cse562.model.data.ResultSet;
import edu.buffalo.cse562.model.operatorabstract.Operator;
import edu.buffalo.cse562.model.operators.*;
import edu.buffalo.cse562.parser.datavisitors.FromItemVisitorImpl;
import edu.buffalo.cse562.parser.datavisitors.OrderByVisitorImpl;
import edu.buffalo.cse562.parser.datavisitors.SelectItemVisitorImpl;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.ArrayList;
import java.util.List;

public class TreeMaker {
    private ArrayList<Operator> operatorList;
    private ResultSet resultSet;
    private DataGrabber dataGrabber;

    public TreeMaker(DataGrabber dataGrabber) {
        this.dataGrabber = dataGrabber;
        this.operatorList = new ArrayList<>();
    }

    public void makeTree(PlainSelect plainSelect) {
        addSourceOperator(plainSelect);
        addWhereOperator(plainSelect);
        addProjectOperator(plainSelect);
        addOrderByOperator(plainSelect);
        addGroupbyOperator(plainSelect);
    }

    /* TODO
    private List selectItems;
    private FromItem fromItem;
    private Expression where;
    private List groupByColumnReferences;
    private List orderByElements;*/

    public void addSourceOperator(PlainSelect plainSelect) {
        FromOperator fromOperator = new FromOperator(dataGrabber);

        FromItemVisitorImpl fromItemVisitor = new FromItemVisitorImpl(fromOperator);
        plainSelect.getFromItem().accept(fromItemVisitor);

        fromOperator.dataIn(null);
        resultSet = fromOperator.dataOut();

        List<Join> joins = plainSelect.getJoins();
        if (joins != null) {
            JoinOperator joinOperator;
            for (Join join : joins) {
                joinOperator = new JoinOperator(join);
                joinOperator.dataIn(resultSet);
                resultSet = joinOperator.dataOut();
            }
        }
    }

    public void addWhereOperator(PlainSelect plainSelect) {
        //todo
        if (plainSelect.getWhere() != null) {
            SelectionOperator selectOperator = new SelectionOperator();
            selectOperator.setWhereCondition(plainSelect.getWhere());
            this.operatorList.add(selectOperator);
        }
    }

    public void addProjectOperator(PlainSelect plainSelect) {
        ProjectionOperator projectionOperator = new ProjectionOperator();
        List<SelectItem> selectItems = plainSelect.getSelectItems();
        SelectItemVisitorImpl selectItemVisitorImpl = new SelectItemVisitorImpl(projectionOperator);

        for (SelectItem item : selectItems) {
            item.accept(selectItemVisitorImpl);
        }
        operatorList.add(projectionOperator);
    }

    public void addOrderByOperator(PlainSelect plainSelect) {
        OrderByVisitorImpl orderByVisitorImpl;
        ArrayList<Expression> orderByExpression = new ArrayList<>();
        List<OrderByElement> orderByElements = plainSelect.getOrderByElements();

        if (orderByElements == null || orderByElements.isEmpty()) {
            return;
        }

        for (OrderByElement orderByElement : orderByElements) {
            orderByVisitorImpl = new OrderByVisitorImpl();
            orderByElement.accept(orderByVisitorImpl);
            orderByExpression.add(orderByVisitorImpl.getExpression());
        }
        operatorList.add(new OrderByOperator(orderByExpression));
    }

    public void addGroupbyOperator(PlainSelect plainSelect) {
        /**
         * A list of {@link Expression}s of the GROUP BY clause.
         * It is null in case there is no GROUP BY clause
         * @return a list of {@link Expression}s
         */
        List<Expression> columnReference = plainSelect.getGroupByColumnReferences();
        // Inaccurate documentation about this.......
        // Sysout to see what we get here
    }

/*    public ResultSet execute(ResultSet resultSet) {
        for (Operator Ops : this.operatorList) {
    		Ops.dataIn(resultSet);
    		resultSet = Ops.dataOut();
    	}
    	return ResultSet;
    }
*/
}
