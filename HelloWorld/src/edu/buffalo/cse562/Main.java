package edu.buffalo.cse562;

import edu.buffalo.cse562.indexer.visitors.IndexingStatementVisitor;
import edu.buffalo.cse562.operator.Operator;
import edu.buffalo.cse562.visitor.MyStatementVisitor;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {

        int i;
        File dataDir = null;
        File swapDir = null;
        File indexDir = null;
        Boolean isBuildPhase = false;
        ArrayList<File> sqlFiles = new ArrayList<>();
        for (i = 0; i < args.length; i++) {
            if (args[i].equals("--data")) {
                dataDir = new File(args[i + 1]);
                i++;
            } else if (args[i].equals("--swap")) {
                swapDir = new File(args[i + 1]);
                i++;
            } else if (args[i].equals("--index")) {
                indexDir = new File(args[i + 1]);
            } else if (args[i].equals("--build")) {
                isBuildPhase = true;
            } else {
                sqlFiles.add(new File(args[i]));
            }
        }

        if (isBuildPhase) {
            IndexingStatementVisitor visitor  = new IndexingStatementVisitor();
            executeSqls(swapDir, sqlFiles, visitor, null);
        } else {
            MyStatementVisitor myVisitor = new MyStatementVisitor(dataDir, swapDir, indexDir);
            executeSqls(swapDir, sqlFiles, myVisitor, myVisitor.source);
        }
    }

    private static void executeSqls(File swapDir, ArrayList<File> sqlFiles, StatementVisitor myVisitor, Operator source) {
        for (File sqlFile : sqlFiles) {
            try (FileReader reader = new FileReader(sqlFile)) {
                CCJSqlParser parser = new CCJSqlParser(reader);
                Statement stmnt;
                while ((stmnt = parser.Statement()) != null) {
                    stmnt.accept(myVisitor);
                    View.dump(source);
                    cleanSwap(swapDir);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void cleanSwap(File swapDir) {
        String[] myFiles;
        if (swapDir != null && swapDir.isDirectory()) {
            myFiles = swapDir.list();
            for (int i = 0; i < myFiles.length; i++) {
                File myFile = new File(swapDir, myFiles[i]);
                myFile.delete();
            }
        }
    }

}
