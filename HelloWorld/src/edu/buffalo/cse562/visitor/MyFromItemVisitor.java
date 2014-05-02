package edu.buffalo.cse562.visitor;

import edu.buffalo.cse562.model.TableInfo;
import edu.buffalo.cse562.operator.Operator;
import edu.buffalo.cse562.operator.ScanOperator;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.io.File;
import java.util.HashMap;

public class MyFromItemVisitor implements FromItemVisitor {
    private File dataDir;
    private File swapDir;

    public Operator source;
    public ColumnSchema[] finalSchema;

    
    public HashMap<String, TableInfo> tables;

    public MyFromItemVisitor(File dataDir, File swapDir, HashMap<String, TableInfo> tables, ColumnSchema[] finalSchema) {
        this.dataDir = dataDir;
        this.swapDir = swapDir;
        this.tables = tables;
        this.finalSchema = finalSchema;
    }


	@Override
    public void visit(Table table) {
        final TableInfo tableInfo = tables.get(table.getName());
        tableInfo.setAlias(table.getAlias());
        source = new ScanOperator(dataDir, tableInfo, finalSchema);
    }

    @Override
    public void visit(SubSelect subSelect) {
        MySelectVisitor selectVisitor = new MySelectVisitor(dataDir, swapDir, tables);
        subSelect.getSelectBody().accept(selectVisitor);
        source = selectVisitor.source;
    }

    @Override
    public void visit(SubJoin arg0) {
        throw new UnsupportedOperationException("Subjoin not supported yet! Get coding!");
    }

}
