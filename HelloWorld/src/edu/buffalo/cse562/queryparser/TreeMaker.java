package edu.buffalo.cse562.queryparser;

import edu.buffalo.cse562.model.operators.Operator;
import edu.buffalo.cse562.model.operators.SourceOperator;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TreeMaker {
    private Stack<Operator> rAOperatorStack;

    public TreeMaker() {
        this.rAOperatorStack = new Stack<>();
    }

    public void push() {

    }

    public void create() {

    }

    public void read(PlainSelect plainSelect) {
        SourceOperator sourceOperator = new SourceOperator();
        List<String> tableNames = new ArrayList<>();
        FromItem fromItem;

        if ((fromItem = plainSelect.getFromItem()) != null) {
            String fromTable = ((Table) fromItem).getName();
            tableNames.add(fromTable);
            sourceOperator.setTableName(tableNames);
        }

        sourceOperator.setWhereCondition(plainSelect.getWhere());
    }
}
