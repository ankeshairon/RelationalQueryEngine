package edu.buffalo.cse562.invoker;

import edu.buffalo.cse562.parser.StatementVisitorImpl;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class RelationalQueryEngine {


    private String dataFolderName;
    private String sqlQueryFileName;

    public RelationalQueryEngine(String dataFolderName, String sqlQueryFileName) {
        this.dataFolderName = dataFolderName;
        this.sqlQueryFileName = sqlQueryFileName;
    }

    public void invoke() {
        try {
            Statement sqlStatement;
            String result;

            CCJSqlParser sqlParser = new CCJSqlParser(new FileReader(new File(sqlQueryFileName)));
            StatementVisitorImpl statementEvaluator = new StatementVisitorImpl(dataFolderName);

            while ((sqlStatement = sqlParser.Statement()) != null) {
                sqlStatement.accept(statementEvaluator);

                if ((result = statementEvaluator.getResult()) != null) {
                    System.out.println(result);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Unable to find file : " + sqlQueryFileName);
        } catch (ParseException e) {
            System.out.println("Something went wrong while parsing the query. Please try again");
        }
    }
}