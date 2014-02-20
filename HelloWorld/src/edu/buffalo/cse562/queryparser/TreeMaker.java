/*
 * @Author:  Dev Bharadwaj
 * Pass-in the PlainSelect object and create the logical list of operators
 * execute() is then called and data is passed in and out of the operators
 * We return the output back to PlainSelect.
 */

package edu.buffalo.cse562.queryparser;

import edu.buffalo.cse562.mock.Datum;
import edu.buffalo.cse562.model.operatorabstract.Operator;
import edu.buffalo.cse562.model.operators.JoinOperator;
import edu.buffalo.cse562.model.operators.OrderByOperator;
import edu.buffalo.cse562.model.operators.ProjectionOperator;
import edu.buffalo.cse562.model.operators.SelectionOperator;
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
    private ArrayList<Operator> rAOperatorList;
    private Datum datum;

    public TreeMaker(Datum datum) {
        this.datum = datum;
        this.rAOperatorList = new ArrayList<>();
    }

    public void makeTree(PlainSelect plainSelect) {
        addSourceOperator(plainSelect);
        addWhereOperator(plainSelect);
        addProjectOperator(plainSelect);
        addOrderbyOperator(plainSelect);
        addGroupbyOperator(plainSelect);
    }

    /* TODO
    private List selectItems;
    private FromItem fromItem;
    private Expression where;
    private List groupByColumnReferences;
    private List orderByElements;*/

    public void addSourceOperator(PlainSelect plainSelect) {
        FromItemVisitorImpl fromItemVisitor = new FromItemVisitorImpl();
        plainSelect.getFromItem().accept(fromItemVisitor);
        datum = fromItemVisitor.getDatum();

        List<Join> joins = plainSelect.getJoins();
        if (joins != null) {
            JoinOperator joinOperator;
            for (Join join : joins) {
                joinOperator = new JoinOperator(join);
                joinOperator.dataIn(datum);
                datum = joinOperator.dataOut();
            }
        }
    }

    public void addWhereOperator(PlainSelect plainSelect) {
        //todo
        if (plainSelect.getWhere() != null) {
            SelectionOperator selectOperator = new SelectionOperator();
            selectOperator.setWhereCondition(plainSelect.getWhere());
            this.rAOperatorList.add(selectOperator);
        }
    }

    public void addProjectOperator(PlainSelect plainSelect) {
        ProjectionOperator projectionOperator = new ProjectionOperator(null);
        List<SelectItem> selectItem = plainSelect.getSelectItems();

        SelectItemVisitorImpl selectItemVisitorImpl;
        for (SelectItem item : selectItem) {
            selectItemVisitorImpl = new SelectItemVisitorImpl();
            item.accept(selectItemVisitorImpl);
            if (selectItemVisitorImpl.getIfAllColumns() == 1) {
//                projectionOperator.getDataForAllColumns();
            } else if (selectItemVisitorImpl.getAllTableColumns() != null) {
                // get string of specified columns
            } else if (selectItemVisitorImpl.getExpression() != null) {
                // get expression and evaluate it
                // An expression as in "SELECT expr1 AS EXPR", get alias and expression
            }
        }
        this.rAOperatorList.add(new ProjectionOperator(null));
    }

    public void addOrderbyOperator(PlainSelect plainSelect) {
        OrderByVisitorImpl orderByVisitorImpl;
        ArrayList<Expression> orderByExpression = new ArrayList<>();
        List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
        for (OrderByElement e : orderByElements) {
            orderByVisitorImpl = new OrderByVisitorImpl();
            e.accept(orderByVisitorImpl);
            orderByExpression.add(orderByVisitorImpl.getExpression());
        }
        this.rAOperatorList.add(new OrderByOperator(orderByExpression));
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

/*    public Datum execute(Datum datum) {
        for (Operator Ops : this.rAOperatorList) {
    		Ops.dataIn(datum);
    		datum = Ops.dataOut();
    	}
    	return Datum;
    }
*/
}
