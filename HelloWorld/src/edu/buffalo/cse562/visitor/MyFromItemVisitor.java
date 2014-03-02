package edu.buffalo.cse562.visitor;

import edu.buffalo.cse562.operator.Operator;
import edu.buffalo.cse562.operator.ScanOperator;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class MyFromItemVisitor implements FromItemVisitor {
    public File dataDir;
    public Operator source;
    public ColumnSchema[] finalSchema;

    public HashMap<String, List<ColumnDefinition>> tables;

    public MyFromItemVisitor(File dataDir, HashMap<String, List<ColumnDefinition>> tables, ColumnSchema[] finalSchema) {
        this.dataDir = dataDir;
        this.tables = tables;
        this.finalSchema = finalSchema;
    }

    @Override
    public void visit(Table tbl) {
        source = new ScanOperator(dataDir, tbl.getName(), tables, finalSchema);
    }

    @Override
    public void visit(SubSelect subSelect) {
        MySelectVisitor selectVisitor = new MySelectVisitor(dataDir, tables);
        subSelect.getSelectBody().accept(selectVisitor);
        source = selectVisitor.source;
    }

    @Override
    public void visit(SubJoin arg0) {


    }

}
