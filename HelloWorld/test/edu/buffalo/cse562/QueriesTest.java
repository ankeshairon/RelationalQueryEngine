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

    public QueriesTest(String query) {
        this.query = query;
    }

    @Test
    public void checkpoint2_little_data() throws Exception {
        print(query);
        testForExpectedData(query, LITTLE);
    }

    private void testForExpectedData(String sqlFileName, String size) throws IOException, InterruptedException {
        String folderName;
        String[] args = new String[]{"--data", "resources/little/data_files", "sqlFiles/tpch_schemas.sql", "resources/sql_query/" + sqlFileName + ".sql", "--swap", "resources/swap", "--index", "index"};
        folderName = "resources/little/expected/";

        invokeTestClassWithArgs(args);

        final String actualResult = outContent.toString().trim();

        String expectedData = getExpectedData(folderName, sqlFileName).trim();

//        assertEquals("", errContent.toString());
        assertEquals(expectedData, actualResult);
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
                        {"tpch1"},
                        {"tpch3"},
                        {"tpch5"},
                        {"tpch6"},
                        {"tpch07a"},
                        {"tpch07b"},
                        {"tpch07c"},
                        {"tpch07d"},
                        {"tpch07e"},
                        {"tpch07f"},
                        {"tpch07g"},
                        {"tpch10a"},
                        {"tpch10b"},
                        {"tpch10c"},
                        {"tpch10d"},
                        {"tpch12a"},
                        {"tpch12b"},
                        {"tpch12c"},
                        {"tpch12d"},
                        {"tpch16a"},
                        {"tpch16b"},
                        {"tpch16c"},
                        {"tpch16d"}
                };

        return Arrays.asList(queries);

    }

}
