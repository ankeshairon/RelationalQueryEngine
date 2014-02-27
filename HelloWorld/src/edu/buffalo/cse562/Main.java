package edu.buffalo.cse562;


import edu.buffalo.cse562.visitor.MyStatementVisitor;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {

        int i;
        File dataDir = null;
        ArrayList<File> sqlFiles = new ArrayList<File>();
        for (i = 0; i < args.length; i++) {
            if (args[i].equals("--data")) {
            	System.out.println(System.getProperty("user.dir") + "/" + args[i+1]);
            	String current = new java.io.File( "." ).getCanonicalPath();
                dataDir = new File(current + "/" + args[i + 1]);
                i++;
            } else {
                sqlFiles.add(new File(args[i]));
            }
        }

        MyStatementVisitor myVisitor = new MyStatementVisitor(dataDir.getName());

        for (File sqlFile : sqlFiles) {
            try (FileReader reader = new FileReader(sqlFile)) {
                CCJSqlParser parser = new CCJSqlParser(reader);
                Statement stmnt;
                while ((stmnt = parser.Statement()) != null) {
                    stmnt.accept(myVisitor);
                    View.dump(myVisitor.source);
                }

            } catch (IOException e) {
                e.printStackTrace();
                new Throwable("CUSTOM EXCEPTION : " + Main.class.getName());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

}
