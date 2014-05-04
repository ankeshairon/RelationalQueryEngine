package edu.buffalo.cse562;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

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

@RunWith(value = Parameterized.class)
public class QueriesTest extends MainTest {


    private String query;
    public final static String dataDirPath = "resources/little/data_files";
    public final static String sqlDirPath = "resources/sql_query/";
    private final static String indexDirPath = "index";
    private final static String expectedDataFolderPath = "resources/little/expected/";

    public QueriesTest(String query) {
        this.query = query;
    }

    @Test
    public void checkpoint2_little_data() throws Exception {
        print(query);
        testForExpectedData(query, LITTLE);
    }

    private void testForExpectedData(String sqlFileName, String size) throws IOException, InterruptedException {
        String[] args = new String[]{"--data", dataDirPath, "sqlFiles/tpch_schemas.sql", sqlDirPath + sqlFileName + ".sql", "--swap", "resources/swap", "--index", indexDirPath};

        final String log = invokeTestClassWithArgs(args, sqlFileName);

        final String actualResult = outContent.toString().trim();

        String expectedData = getExpectedData(expectedDataFolderPath, sqlFileName).trim();

//        assertEquals("", errContent.toString());
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

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] queries =
                {
                        {"tpch1"},            //   0
                        {"tpch3"},            //   1
                        {"tpch5"},            //   2
                        {"tpch6"},            //   3
                        {"tpch07a"},          // 4
                        {"tpch07b"},          // 5
                        {"tpch07c"},          // 6
                        {"tpch07d"},          // 7
                        {"tpch07e"},          // 8
                        {"tpch07f"},          // 9
                        {"tpch07g"},          // 10
                        {"tpch10a"},          // 11
                        {"tpch10b"},          // 12
                        {"tpch10c"},          // 13
                        {"tpch10d"},          // 14
                        {"tpch12a"},          // 15
                        {"tpch12b"},          // 16
                        {"tpch12c"},          // 17
                        {"tpch12d"},          // 18
                        {"tpch16a"},          // 19
                        {"tpch16b"},          // 20
                        {"tpch16c"},          // 21
                        {"tpch16d"}           // 22
                };

        return Arrays.asList(queries);

    }

}
