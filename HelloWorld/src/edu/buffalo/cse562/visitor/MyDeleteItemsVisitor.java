package edu.buffalo.cse562.visitor;


import edu.buffalo.cse562.model.TableInfo;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Table;

import java.beans.Expression;
import java.io.*;
import java.util.HashMap;

public class MyDeleteItemsVisitor {
    File dataDir;
    private final HashMap<String, TableInfo> tablesInfo;

    public MyDeleteItemsVisitor(File dataDir, HashMap<String, TableInfo> tablesInfo) {
        this.dataDir = dataDir;
        this.tablesInfo = tablesInfo;
    }

    public void deleteFrom(Table table, Expression expression){
        if(tablesInfo.containsKey(table.getName())){
            EvaluatorDeletion eval = new EvaluatorDeletion();
            File originalFile = new File(dataDir.getAbsolutePath()+"//"+table.getName()+".dat");
            File tempFile = new File(dataDir.getAbsolutePath()+"//"+table.getName()+".tmp");
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(originalFile));
                PrintWriter writer = new PrintWriter(new FileWriter(tempFile));
                String line = null;
                while((line = reader.readLine()) != null){
                    if(expression.accept(eval).returnFlag){     // The logic involves processing only the required
                                                     // tuples and eliminating the others
                        writer.println(line);
                        writer.flush();
                    }
                }
                reader.close();
                writer.close();
                tempFile.renameTo(originalFile);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}