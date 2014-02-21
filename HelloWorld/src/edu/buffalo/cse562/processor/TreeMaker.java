/*
 * @Author:  Dev Bharadwaj
 * Pass-in the PlainSelect object and create the logical list of operators
 * execute() is then called and data is passed in and out of the operators
 * We return the output back to PlainSelect.
 */

package edu.buffalo.cse562.processor;

import edu.buffalo.cse562.datagrabber.DataGrabber;
import edu.buffalo.cse562.model.data.ResultSet;
import edu.buffalo.cse562.model.operatorabstract.Operator;
import edu.buffalo.cse562.model.operators.OrderByOperator;
import edu.buffalo.cse562.model.operators.ProjectionOperator;
import edu.buffalo.cse562.model.operators.SelectionOperator;
import edu.buffalo.cse562.model.operators.sourceoperators.FromOperator;
import edu.buffalo.cse562.model.operators.sourceoperators.JoinOperator;
import edu.buffalo.cse562.model.operators.sourceoperators.SourceOperator;
import edu.buffalo.cse562.parser.datavisitors.FromItemVisitorImpl;
import edu.buffalo.cse562.parser.datavisitors.OrderByVisitorImpl;
import edu.buffalo.cse562.parser.datavisitors.SelectItemVisitorImpl;
import edu.buffalo.cse562.view.ResultsViewer;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.ArrayList;
import java.util.List;

public class TreeMaker {
    private ArrayList<Operator> operatorList;
    private DataGrabber dataGrabber;

    public TreeMaker(DataGrabber dataGrabber) {
        this.dataGrabber = dataGrabber;
        this.operatorList = new ArrayList<>();
    }

    public void execute(PlainSelect plainSelect) {
        addSourceOperator(plainSelect);
        addWhereOperator(plainSelect);
        addProjectOperator(plainSelect);
        addOrderByOperator(plainSelect);
        addGroupByOperator(plainSelect);
        ResultsViewer.viewResults(executeOperators(null));

    }


    /**
     * __________________________________________________________________________________________________
     * SQL        |                     Java                               |   Target Checkpoint
     * ----------------|--------------------------------------------------------|------------------------
     * From T       |   visit(Table table) in FromVisitorItemImpl            |       1
     * T1 JOIN T2     |   visit(SubJoin subjoin) in FromVisitorItemImpl        |       Not decided
     * T1,T2,T3     |   getJoins() return {T2,T3}                            |       1
     * subexpression |   visit(SubSelect subselect) in FromVisitorItemImpl    |       1
     */
    public void addSourceOperator(PlainSelect plainSelect) {
        SourceOperator sourceOperator = new SourceOperator();

        FromOperator fromOperator = new FromOperator(dataGrabber);
        FromItemVisitorImpl fromItemVisitor = new FromItemVisitorImpl(fromOperator, dataGrabber);
        plainSelect.getFromItem().accept(fromItemVisitor);
        sourceOperator.addSubOperator(fromOperator);
//        ResultsViewer.viewResults(fromOperator.dataOut());

        List<Join> joins = plainSelect.getJoins();
        if (joins != null) {
            for (Join join : joins) {
                sourceOperator.addSubOperator(new JoinOperator(join));
            }
        }

        operatorList.add(sourceOperator);
    }

    public void addWhereOperator(PlainSelect plainSelect) {
        //todo

        Expression whereExpression = plainSelect.getWhere();
        if (whereExpression != null) {
            SelectionOperator selectOperator = new SelectionOperator();
            selectOperator.setWhereCondition(whereExpression);
            this.operatorList.add(selectOperator);
        }
    }

    public void addProjectOperator(PlainSelect plainSelect) {
        ProjectionOperator projectionOperator = new ProjectionOperator(dataGrabber, ((Table) plainSelect.getFromItem()).getName());
        List<SelectItem> selectItems = plainSelect.getSelectItems();
        SelectItemVisitorImpl selectItemVisitorImpl = new SelectItemVisitorImpl(projectionOperator);

        for (SelectItem item : selectItems) {
            item.accept(selectItemVisitorImpl);
        }
        operatorList.add(projectionOperator);
    }

    public void addOrderByOperator(PlainSelect plainSelect) {
        OrderByVisitorImpl orderByVisitorImpl = new OrderByVisitorImpl();
        ArrayList<Expression> orderByExpressions = new ArrayList<>();
        List<OrderByElement> orderByElements = plainSelect.getOrderByElements();

        if (orderByElements == null || orderByElements.isEmpty()) {
            return;
        }

        for (OrderByElement orderByElement : orderByElements) {
            orderByElement.accept(orderByVisitorImpl);
            orderByExpressions.add(orderByVisitorImpl.getExpression());
        }
        operatorList.add(new OrderByOperator(orderByExpressions));
    }

    public void addGroupByOperator(PlainSelect plainSelect) {
        /**
         * A list of {@link Expression}s of the GROUP BY clause.
         * It is null in case there is no GROUP BY clause
         * @return a list of {@link Expression}s
         */
        List<Expression> columnReference = plainSelect.getGroupByColumnReferences();
        // Inaccurate documentation about this.......
        // Sysout to see what we get here
    }

    private ResultSet executeOperators(ResultSet resultSet) {
        for (Operator operator : this.operatorList) {
            operator.dataIn(resultSet);
            resultSet = operator.dataOut();
        }
        return resultSet;
    }
}
