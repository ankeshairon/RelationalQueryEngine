package edu.buffalo.cse562.invoker;

import edu.buffalo.cse562.parser.datavisitors.StatementDataVisitorImpl;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class RelationalQueryEngine {

    public void invoke(String[] args) {
        String dataFolderName = args[1];
        String sqlQueryFileName;
        Statement sqlStatement;
        String result;

        for (int index = 2; index < args.length; index++) {
            sqlQueryFileName = args[index];
            try {
                CCJSqlParser sqlParser = new CCJSqlParser(new FileReader(new File(sqlQueryFileName)));
                StatementDataVisitorImpl statementEvaluator = new StatementDataVisitorImpl(dataFolderName);

                while ((sqlStatement = sqlParser.Statement()) != null) {
                    sqlStatement.accept(statementEvaluator);
                }
            } catch (FileNotFoundException e) {
                System.out.println("Unable to find the sql file : " + sqlQueryFileName);
            } catch (ParseException e) {
                System.out.println("Something went wrong while parsing the query. Please try again");
            }
        }
    }
}