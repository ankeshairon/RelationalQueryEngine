package edu.buffalo.cse562.visitor;


import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.data.Datum.CastException;
import edu.buffalo.cse562.operator.JoinOperator;
import edu.buffalo.cse562.operator.Operator;
import edu.buffalo.cse562.operator.ProjectionOperator;
import edu.buffalo.cse562.operator.SelectionOperator;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.select.*;

import java.util.HashMap;
import java.util.List;

public class MySelectVisitor implements SelectVisitor {

    public String dataDir;

    HashMap<String, List<ColumnDefinition>> tables;

    public MySelectVisitor(String dataDir, HashMap<String, List<ColumnDefinition>> tables) {
        this.dataDir = dataDir;
        this.tables = tables;
    }

    @Override
    public void visit(PlainSelect stmnt) {
        MyFromItemVisitor myFromItemVisitor = new MyFromItemVisitor(dataDir, tables);

        FromItem fromItem = stmnt.getFromItem();
        fromItem.accept(myFromItemVisitor);

        Operator oper = myFromItemVisitor.source;
        List<Join> joins;
        if ((joins = stmnt.getJoins()) != null) {
            for (Join join : joins) {
                fromItem = join.getRightItem();
                fromItem.accept(myFromItemVisitor);
                Operator newOper = myFromItemVisitor.source;
                oper = new JoinOperator(oper, newOper);
            }
        }

        List<SelectItem> selItems = stmnt.getSelectItems();
        Operator ops = new SelectionOperator(oper,oper.getSchema(),stmnt.getWhere());
        //oper = new ProjectionOperator(ops, selItems);

        dump(ops);

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
                    } catch (CastException e) {
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
                    } catch (CastException e) {
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
