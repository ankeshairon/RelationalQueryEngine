package edu.buffalo.cse562.indexer.visitors;

import edu.buffalo.cse562.indexer.model.TableIndexingInfo;
import edu.buffalo.cse562.visitor.optimizer.CrossToJoinOptimizer;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IndexingStatementVisitor implements StatementVisitor, SelectVisitor, FromItemVisitor {

    //tablename-tableinfo
    private Map<String, TableIndexingInfo> tableIndexingInfos;

    public IndexingStatementVisitor() throws IOException {
        tableIndexingInfos = new HashMap<>();
    }

    @Override
    public void visit(Select select) {
        select.getSelectBody().accept(this);
    }

    @Override
    public void visit(Delete delete) {
    }

    @Override
    public void visit(Update update) {
        final String tableName = update.getTable().getName().toLowerCase();

        final Set<Column> allColumns = new CrossToJoinOptimizer(update.getWhere()).getAllColumnsUsedInWhereClause();
        allColumns.addAll(update.getColumns());

        for (Column column : allColumns) {
            tableIndexingInfos.get(tableName).addIndex(column);
        }
    }

    @Override
    public void visit(Insert insert) {
    }

    @Override
    public void visit(Replace replace) {
    }

    @Override
    public void visit(Drop drop) {
    }

    @Override
    public void visit(Truncate truncate) {
    }

    @Override
    public void visit(CreateTable createTable) {
        final String tableName = createTable.getTable().getName().toLowerCase();
        final TableIndexingInfo tableIndexingInfo = new TableIndexingInfo(tableName, createTable.getColumnDefinitions(), null);

        tableIndexingInfos.put(tableName, tableIndexingInfo);
    }

    public Map<String, TableIndexingInfo> getTableIndexingInfos() {
        return tableIndexingInfos;
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        plainSelect.getFromItem().accept(this);

        final List<Join> joins = plainSelect.getJoins();
        if (joins != null) {
            for (Join join : joins) {
                join.getRightItem().accept(this);
            }
        }


        final Expression where = plainSelect.getWhere();
        if (where == null) {
            return;
        }

        final Set<Column> allColumns = new CrossToJoinOptimizer(where).getAllColumnsUsedInWhereClause();

        String tableName = null;
        if (allColumns.iterator().next().getTable().getName() == null) {
            tableName = plainSelect.getFromItem().toString().toLowerCase();
        }

        if (tableName == null) {
            for (Column column : allColumns) {
                tableIndexingInfos.get(column.getTable().getName().toLowerCase()).addIndex(column);
            }
        } else {
            final TableIndexingInfo tableIndexingInfo = tableIndexingInfos.get(tableName);
            for (Column column : allColumns) {
                tableIndexingInfo.addIndex(column);
            }
        }
    }

    @Override
    public void visit(Union union) {
    }

    @Override
    public void visit(Table table) {
        String alias = table.getAlias();
        if (alias != null) {
            tableIndexingInfos.put(alias, tableIndexingInfos.get(table.getName()));
        }
    }

    @Override
    public void visit(SubSelect subSelect) {
        subSelect.getSelectBody().accept(this);
    }

    @Override
    public void visit(SubJoin subjoin) {
    }
}
