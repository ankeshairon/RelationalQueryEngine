package edu.buffalo.cse562.visitor;


import edu.buffalo.cse562.model.TableInfo;
import edu.buffalo.cse562.operator.*;
import edu.buffalo.cse562.operator.aggregation.AggregationOperator;
import edu.buffalo.cse562.schema.ColumnSchema;
import edu.buffalo.cse562.visitor.optimizer.JoinMaker;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MySelectVisitor implements SelectVisitor {

    public Operator source;
    private ColumnSchema[] finalSchema;
    private final File dataDir;
    private final File swapDir;
    private final HashMap<String, TableInfo> tablesInfo;

    public MySelectVisitor(File dataDir, File swapDir, HashMap<String, TableInfo> tablesInfo) {
        this.dataDir = dataDir;
        this.swapDir = swapDir;
        this.tablesInfo = tablesInfo;
    }

    @Override
    public void visit(Union stmnt) {
        List<PlainSelect> plainSelects = stmnt.getPlainSelects();
        for (PlainSelect plainSelect : plainSelects) {
            visit(plainSelect);
        }
    }

    @Override
    public void visit(PlainSelect statement) {
        MyFromItemVisitor myFromItemVisitor = new MyFromItemVisitor(dataDir, swapDir, tablesInfo, finalSchema);

        visitFromItems(statement, myFromItemVisitor);
        JoinMaker joinMaker = visitMultipleFromItems(statement, myFromItemVisitor);
        applyWhereConditions(joinMaker, statement.getWhere());
        createItemsToProject(statement);
        orderTheResults(statement);
        limitSize(statement);
    }

    private void limitSize(PlainSelect statement) {
        if (statement.getLimit() != null) {
            source = new LimitOperator(source, statement.getLimit());
        }
    }

    private void orderTheResults(PlainSelect statement) {
        List<OrderByElement> orderByElements = statement.getOrderByElements();
        if (orderByElements != null) {
            MyOrderByVisitor orderByVisitor = new MyOrderByVisitor(source);
            for (OrderByElement orderByElement : orderByElements) {
                orderByElement.accept(orderByVisitor);
            }
//            source = new ExternalSort(source, orderByVisitor.indexesOfColumnsToSortOn,swapDir);
            source = new OrderByOperator(source, orderByVisitor.indexesOfColumnsToSortOn);
        }
    }

    private void applyWhereConditions(JoinMaker joinMaker, Expression where) {
        if (joinMaker != null) {
            List<Expression> nonExclusiveConditionClauses = joinMaker.getNonExclusiveConditionClauses();
            if (nonExclusiveConditionClauses != null && !nonExclusiveConditionClauses.isEmpty()) {
                source = new SelectionOperator(source, nonExclusiveConditionClauses);
            }
        } else if (where != null) {
            source = new SelectionOperator(source, Arrays.asList(where));
        }
    }

    private void createItemsToProject(PlainSelect statement) {
        List<SelectItem> selectItems = statement.getSelectItems();
        if (selectItems != null) {
            MySelectItemVisitor selectItemVisitor = new MySelectItemVisitor(source);
            for (SelectItem selectItem : selectItems) {
                selectItem.accept(selectItemVisitor);
            }
            ColumnSchema[] outputSchema = new ColumnSchema[selectItemVisitor.outputSchema.size()];
            selectItemVisitor.outputSchema.toArray(outputSchema);
            finalSchema = outputSchema;

            Integer[] indexArray = new Integer[selectItemVisitor.indexes.size()];
            selectItemVisitor.indexes.toArray(indexArray);

            if (selectItemVisitor.isAggregationPresent()) {
                source = new AggregationOperator(source, outputSchema, indexArray, statement);
            } else {
                //todo need to implement distinct for the case when no aggregations present
                source = new ProjectionOperator(source, outputSchema, indexArray);
            }
        }
    }

    private void visitFromItems(PlainSelect statement, MyFromItemVisitor myFromItemVisitor) {
        FromItem fromItem = statement.getFromItem();
        fromItem.accept(myFromItemVisitor);
        source = myFromItemVisitor.source;
    }

    private JoinMaker visitMultipleFromItems(PlainSelect statement, MyFromItemVisitor myFromItemVisitor) {
        Expression where = statement.getWhere();

        List<Join> joins = statement.getJoins();
        if (joins != null) {
            List<Operator> inputOperators = new ArrayList<>();
            inputOperators.add(source);

            for (Join join : joins) {
                join.getRightItem().accept(myFromItemVisitor);
                inputOperators.add(myFromItemVisitor.source);
            }

            JoinMaker joinMaker = new JoinMaker(where, inputOperators, swapDir);
            source = joinMaker.getOptimizedChainedJoinOperator();
            return joinMaker;
        }
        return null;
    }

}
