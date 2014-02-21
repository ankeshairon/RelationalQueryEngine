package edu.buffalo.cse562.parser.datavisitors;


import edu.buffalo.cse562.datagrabber.DataGrabber;
import edu.buffalo.cse562.parser.defaultimpl.AbstractStatementVisitor;
import edu.buffalo.cse562.processor.DataProcessor;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.Select;

import java.util.ArrayList;

public class StatementDataVisitorImpl extends AbstractStatementVisitor {

    private DataGrabber dataGrabber;

    public StatementDataVisitorImpl(String dataFolderName) {
        dataGrabber = new DataGrabber(dataFolderName);
    }

    @Override
    public void visit(Select selectTypeQuery) {
        SelectDataVisitorImpl selectVisitor = new SelectDataVisitorImpl(new DataProcessor(dataGrabber));
        selectTypeQuery.getSelectBody().accept(selectVisitor);
    }

    @Override
    public void visit(CreateTable createTypeQuery) {
        String tableName = createTypeQuery.getTable().getName();
        ArrayList<ColumnDefinition> columnNames = new ArrayList<>();
        for (Object columnDefinition : createTypeQuery.getColumnDefinitions()) {
            columnNames.add(((ColumnDefinition) columnDefinition));
        }
        dataGrabber.addTable(tableName, columnNames);
    }


}
