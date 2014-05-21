package edu.buffalo.cse562.visitor;

import edu.buffalo.cse562.indexer.service.IndexService;
import edu.buffalo.cse562.model.TableInfo;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.statement.insert.Insert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BatchInsertVisitor extends AbstractStatementVisitor {

    private final InsertItemsListVisitor itemsListVisitor;
    private final List<Insert> insertList;
    private HashMap<String, TableInfo> tablesInfo;
    private String tableName;

    public BatchInsertVisitor(HashMap<String, TableInfo> tablesInfo) {
        itemsListVisitor = new InsertItemsListVisitor();
        insertList = new ArrayList<>();
        this.tablesInfo = tablesInfo;
    }

    @Override
    public void visit(Insert insert) {
        if (tableName == null) {
            tableName = insert.getTable().getName().toLowerCase();
        } else {
            assert (tableName.equalsIgnoreCase(insert.getTable().getName()));
        }

        ItemsList itemsList = insert.getItemsList();
        itemsList.accept(itemsListVisitor);
    }

    public void finishPendingOperations() {
        if (insertList.size() == 0) {
            return;
        }

        for (Insert insert : insertList) {
            insert.accept(this);
        }

        final List<String> newTuples = itemsListVisitor.getNewTuples();
        IndexService.getInstance().addTuplesToTable(tablesInfo.get(tableName), newTuples);
    }

    public void addInsertItem(Insert insert) {
        insertList.add(insert);
    }
}
