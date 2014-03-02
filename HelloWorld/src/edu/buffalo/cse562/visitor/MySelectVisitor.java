package edu.buffalo.cse562.visitor;


import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.operator.*;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.select.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class MySelectVisitor implements SelectVisitor {

    public File dataDir;
    public Operator source;
    public ColumnSchema[] finalSchema = null;

    HashMap<String, List<ColumnDefinition>> tables;

    public MySelectVisitor(File dataDir, HashMap<String, List<ColumnDefinition>> tables) {
        this.dataDir = dataDir;
        this.tables = tables;
    }

    @Override
    public void visit(PlainSelect statement) {
        visitFromItems(statement);
        applyWhereConditions(statement);
        createItemsToProject(statement);
        orderTheResults(statement);

    }

    private void orderTheResults(PlainSelect statement) {
        List<OrderByElement> orderByElements = statement.getOrderByElements();
        if (orderByElements != null) {
            MyOrderByVisitor orderByVisitor = new MyOrderByVisitor(source);
            for (OrderByElement orderByElement : orderByElements) {
                orderByElement.accept(orderByVisitor);
            }
            source = new OrderByOperator(source, orderByVisitor.indexesOfColumnsToSortOn);
        }
    }

    private void applyWhereConditions(PlainSelect statement) {
        Expression where = statement.getWhere();
        if (where != null) {
            source = new SelectionOperator(source, source.getSchema(), where);
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
                source = new AggregationOperator(source, outputSchema, indexArray, statement.getGroupByColumnReferences());
            } else {
                source = new ProjectionOperator(source, outputSchema, indexArray);
            }
        }
    }

    private void visitFromItems(PlainSelect statement) {
        MyFromItemVisitor myFromItemVisitor = new MyFromItemVisitor(dataDir, tables, finalSchema);
        FromItem fromItem = statement.getFromItem();
        fromItem.accept(myFromItemVisitor);
        source = myFromItemVisitor.source;

        List<Join> joins = statement.getJoins();
        if (joins != null) {
            for (Join join : joins) {
                fromItem = join.getRightItem();
                fromItem.accept(myFromItemVisitor);
                Operator newOper = myFromItemVisitor.source;
                source = new JoinOperator(source, newOper);
            }
        }
    }

    public void dump(Operator input) {
        Datum[] row = input.readOneTuple();
        while (row != null) {
            for (Datum col : row) {
                if (col.getType() == Datum.type.LONG) {
                    try {
                        long l = col.toLONG();
                        System.out.print(l + "|");
                    } catch (Datum.CastException e) {

                        e.printStackTrace();
                    }
                }
                if (col.getType() == Datum.type.STRING) {
                    String s = col.toSTRING();
                    System.out.print(s + "|");
                }
                if (col.getType() == Datum.type.FLOAT) {
                    try {
                        float f = col.toFLOAT();
                        System.out.print(f + "|");
                    } catch (Datum.CastException e) {

                        e.printStackTrace();
                    }
                }
            }
            System.out.println();
            row = input.readOneTuple();
        }
    }

    @Override
    public void visit(Union stmnt) {
        List<PlainSelect> plainSelects = stmnt.getPlainSelects();
        for (PlainSelect plainSelect : plainSelects) {
            visit(plainSelect);
        }
    }

}
