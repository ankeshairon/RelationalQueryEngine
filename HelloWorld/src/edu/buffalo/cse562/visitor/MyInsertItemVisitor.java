package edu.buffalo.cse562.visitor;


import edu.buffalo.cse562.model.TableInfo;
import edu.buffalo.cse562.operator.Operator;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Table;

import java.io.*;
import java.util.HashMap;

public class MyInsertItemVisitor {
    File dataDir;
    Table table;
    ItemsList itemsList;
    private final HashMap<String, TableInfo> tablesInfo;

    public MyInsertItemVisitor(File dataDir, HashMap<String, TableInfo> tablesInfo){
        this.dataDir = dataDir;
        this.tablesInfo = tablesInfo;
    }

    public void insertInto(Table table, ItemsList itemsList){
        if(tablesInfo.containsKey(table.getWholeTableName())) {



            /*try {
                PrintWriter insertoperator =
                        new PrintWriter(
                                new BufferedWriter(
                                        new FileWriter(dataDir.getAbsolutePath() + "//" + table.getName() + ".dat",
                                                true)
                                )
                        );
                insertoperator.println(itemsList);
                insertoperator.close();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }


}
