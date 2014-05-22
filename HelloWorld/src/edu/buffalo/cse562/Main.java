package edu.buffalo.cse562;

import edu.buffalo.cse562.indexer.IndexBuilder;
import edu.buffalo.cse562.indexer.constants.IndexingConstants;
import edu.buffalo.cse562.indexer.service.IndexService;
import edu.buffalo.cse562.indexer.visitors.IndexingStatementVisitor;
import edu.buffalo.cse562.visitor.MyStatementVisitor;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
                i++;
            } else if (args[i].equals("--build")) {
                isBuildPhase = true;
            } else {
                sqlFiles.add(new File(args[i]));
            }
        }

        if (isBuildPhase) {
            IndexingStatementVisitor visitor = new IndexingStatementVisitor();
//            sqlFiles.addAll(getQueryFiles());
            executeSqls(null, sqlFiles, visitor, indexDir, isBuildPhase);
            new IndexBuilder(visitor.getTableIndexingInfos(), dataDir, indexDir).createIndexes();
        } else {
            MyStatementVisitor myVisitor = new MyStatementVisitor(dataDir, swapDir);
            executeSqls(swapDir, sqlFiles, myVisitor, indexDir, isBuildPhase);
        }
    }

    public static void executeSqls(File swapDir, List<File> sqlFiles, StatementVisitor visitor, File indexDir, Boolean isBuildPhase) {
        CCJSqlParser parser;
        Statement stmnt;

        final IndexService indexService = IndexService.instantiate(indexDir);
        for (File sqlFile : sqlFiles) {
            try (FileReader reader = new FileReader(sqlFile)) {
                parser = new CCJSqlParser(reader);
                executeParsedStatements(visitor, parser);
                if (isBuildPhase) {
                    parseQueriesForOptimization(visitor);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        IndexService.getInstance().commit();
        IndexService.getInstance().close();
    }

    private static void parseQueriesForOptimization(StatementVisitor visitor) throws ParseException {
        InputStream queriesStream = new ByteArrayInputStream(IndexingConstants.queries.getBytes());
        CCJSqlParser sqlParser = new CCJSqlParser(queriesStream);
        executeParsedStatements(visitor, sqlParser);
    }

    private static void executeParsedStatements(StatementVisitor visitor, CCJSqlParser parser) throws ParseException {
        Statement stmnt;
        while ((stmnt = parser.Statement()) != null) {
            stmnt.accept(visitor);
            if (visitor instanceof MyStatementVisitor) {
                View.dump(((MyStatementVisitor) visitor).source);
//                        cleanDir(swapDir);
            }
        }
    }

    private static void cleanDir(File dir) {
        String[] myFiles;
        if (dir != null && dir.isDirectory()) {
            myFiles = dir.list();
            for (String fileName : myFiles) {
                File file = new File(dir, fileName);
                file.delete();
            }
        }
    }

   /* private static List<File> getQueryFiles() {
        final String pathname = "resources/sql_query/";
        final List<File> filesToExecute = new ArrayList<>();

        for (String queryFileName : IndexingConstants.queries) {
            filesToExecute.add(new File(pathname + queryFileName));
        }
        return filesToExecute;
    }
*/
}
