package edu.buffalo.cse562.visitor;


import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.operator.JoinOperator;
import edu.buffalo.cse562.operator.Operator;
import edu.buffalo.cse562.operator.SelectionOperator;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.select.*;

import java.util.HashMap;
import java.util.List;

public class MySelectVisitor implements SelectVisitor {

    public String dataDir;
    public Operator source;

    HashMap<String, List<ColumnDefinition>> tables;

    public MySelectVisitor(String dataDir, HashMap<String, List<ColumnDefinition>> tables) {
        this.dataDir = dataDir;
        this.tables = tables;
    }

    @Override
    public void visit(PlainSelect statement) {
        MyFromItemVisitor myFromItemVisitor = new MyFromItemVisitor(dataDir, tables);
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

        List<SelectItem> selectItems = statement.getSelectItems();
        MySelectItemVisitor selectItemVisitor = new MySelectItemVisitor(source, selectItems.size());
        if (selectItems != null) {
            for (SelectItem selectItem : selectItems) {
                selectItem.accept(selectItemVisitor);
            }
            source = selectItemVisitor.source;
        }

        source = new SelectionOperator(source, source.getSchema(), statement.getWhere());


//		List<SelectItem> selectItems = stmnt.getSelectItems();

//		for(SelectItem selectItem:selectItems){
//			selectItem.accept(mySelectItemVisitor);
//		}
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
                        // TODO Auto-generated catch block
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
                        // TODO Auto-generated catch block
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
