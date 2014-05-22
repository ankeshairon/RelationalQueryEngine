package edu.buffalo.cse562.visitor.optimizer;

import edu.buffalo.cse562.model.TableInfo;
import edu.buffalo.cse562.visitor.ScanOptimizationVisitor;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.select.Select;

import java.util.*;

public class ScanOptimizer {
    private final HashMap<String, TableInfo> tablesInfo;
    private final ScanOptimizationVisitor scanOptimizationVisitor;

    public ScanOptimizer(HashMap<String, TableInfo> tablesInfo, Select statement) {
        this.tablesInfo = tablesInfo;
        scanOptimizationVisitor = new ScanOptimizationVisitor(statement, tablesInfo);
    }

    public void populateRelevantColumnIndexes() {
        final Map<String, Set<Column>> columnsToProject = scanOptimizationVisitor.getColumnsToProject();
        final Map<String, String> aliasNameMap = scanOptimizationVisitor.getAliasNameMap();

        List<ColumnDefinition> allColumns;
        Set<Column> columnsToKeep;
        ArrayList<Integer> indexes;
        TableInfo tableInfo;

        addTableAliasMappingsToTablesInfo(aliasNameMap);

        for (String tableName : columnsToProject.keySet()) {
            tableInfo = tablesInfo.get(tableName.toLowerCase());
            allColumns = tableInfo.getColumnDefinitions();
            columnsToKeep = columnsToProject.get(tableName);
            indexes = getIndexesOfColumnsToProject(allColumns, columnsToKeep);
            Collections.sort(indexes);
            tableInfo.setColumnIndexesUsed(indexes);
        }
    }

    private void addTableAliasMappingsToTablesInfo(Map<String, String> aliasNameMap) {
        TableInfo tableInfo;
        String tableName;

        for (String alias : aliasNameMap.keySet()) {
            tableName = aliasNameMap.get(alias);
            tableInfo = tablesInfo.get(tableName);
            if (tableInfo != null) {
                tablesInfo.put(alias, tableInfo);
            }
        }
    }

    private ArrayList<Integer> getIndexesOfColumnsToProject(List<ColumnDefinition> allColumns, Set<Column> columnsToKeep) {
        ArrayList<Integer> indexes;
        indexes = new ArrayList<>();
        for (Column columnToKeep : columnsToKeep) {
            for (int i = 0; i < allColumns.size(); i++) {
                if (allColumns.get(i).getColumnName().equalsIgnoreCase(columnToKeep.getColumnName())) {
                    indexes.add(i);
                    break;
                }
            }
        }
        return indexes;
    }

}
