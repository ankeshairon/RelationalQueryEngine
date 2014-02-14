package edu.buffalo.cse562.parser;


import edu.buffalo.cse562.datagrabber.DataGrabber;
import edu.buffalo.cse562.parser.defaultimpl.AbstractStatementVisitor;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.Select;

import java.util.ArrayList;

public class StatementVisitorImpl extends AbstractStatementVisitor {

    private String result;
    private DataGrabber dataGrabber;

    public StatementVisitorImpl(String dataFolder) {
        dataGrabber = new DataGrabber(dataFolder);
    }

    public String getResult() {
        return result;
    }

    @Override
    public void visit(Select selectTypeQuery) {
        SelectVisitorImpl selectVisitor = new SelectVisitorImpl(dataGrabber);
        selectTypeQuery.getSelectBody().accept(selectVisitor);
        result = selectVisitor.getResult();
    }

    @Override
    public void visit(CreateTable createTypeQuery) {
        String tableName = createTypeQuery.getTable().getName();
        ArrayList<String> columnNames = new ArrayList<>();
        for (Object columnDefinition : createTypeQuery.getColumnDefinitions()) {
            columnNames.add(((ColumnDefinition) columnDefinition).getColumnName());
        }
        dataGrabber.addTable(tableName, columnNames);
    }


}
