package edu.buffalo.cse562;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Local test setup
 * <p/>
 * How to setup  -
 * - Download LittleBigDataEvaluation.zip from piazza resources
 * - create a following folder structure
 * <p/>
 * Helloworld
 * |---index
 * |        |---<should be emptied after every run>     //todo clear indexes after every run
 * |---resources
 * |        |---little      (for 8mb data)
 * |        |         |---data_files
 * |        |         |         |---<all dat files>
 * |        |         |--expected
 * |        |                   |---<all expected.dat files>
 * |        |---medium      (for 40mb data)
 * |        |        |---data_files
 * |        |        |          |---<all dat files>
 * |        |--expected
 * |        |                   |---<all expected.dat files>
 * |        |---sql_query
 * |        |        |---<all sql query files>
 * |---swap
 * |---<empty directory>
 * - might have to add junit to your path if it's not done by IDE automatically
 */

public class QueriesTestCheckpoint4 extends MainTest {


    public final static String dataDirPath = "resources/normal/data_files";
    public final static String sqlDirPath = "resources/sql_query/";
    public final static String indexDirPath = "index";
    private final static String expectedDataFolderPath = "resources/normal/expected/";
    private String query;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
//        cleanDir(new File(indexDirPath));
//        new QueriesTestPrep().rebuildIndexes(dataDirPath, sqlDirPath);
    }

    @Test
    public void testCheckpoint4Query1() throws Exception {
        query = "query01";
        print(query);
        testForExpectedData(query);
    }

    @Test
    public void testCheckpoint4Query2() throws Exception {
        query = "query02";
        print(query);
        testForExpectedData(query);
    }

    @Test
    public void testCheckpoint4Query3() throws Exception {
        query = "query03";
        print(query);
        testForExpectedData(query);
    }

    @Test
    public void testCheckpoint4Query4() throws Exception {
        query = "query04";
        print(query);
        testForExpectedData(query);
    }

    @Test
    public void testCheckpoint4Query5() throws Exception {
        query = "query05";
        print(query);
        testForExpectedData(query);
    }

    @Test
    public void testCheckpoint4Query6() throws Exception {
        query = "query06";
        print(query);
        testForExpectedData(query);
    }

    private void testForExpectedData(String sqlFileName) throws Exception {
        String[] args = new String[]{"--data", dataDirPath, "sqlFiles/tpch_schemas.sql", sqlDirPath + sqlFileName + ".sql", "--swap", "resources/swap", "--index", indexDirPath};
        final String log = invokeTestClassWithArgs(args, sqlFileName);

        final String actualResult = outContent.toString().trim();
        String expectedData = getExpectedData(expectedDataFolderPath, sqlFileName).trim();

        assertEquals(expectedData, actualResult);
        print(log);
        logTime(log);
    }


    private String getExpectedData(String folderName, String sqlFileName) throws IOException {
        File file = new File(folderName + sqlFileName + ".expected.dat");
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        return new String(data, "UTF-8");
    }

}
