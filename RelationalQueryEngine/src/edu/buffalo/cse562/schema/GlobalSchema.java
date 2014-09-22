package edu.buffalo.cse562.schema;

import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

import java.util.HashMap;
import java.util.List;

public class GlobalSchema {
    public static String dataDir;
    public static HashMap<String, List<ColumnDefinition>> tables;

    public GlobalSchema() {
        tables = new HashMap<String, List<ColumnDefinition>>();
    }
}
