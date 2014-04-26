package edu.buffalo.cse562;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

/**
 * Local test setup
 * <p/>
 * How to setup  -
 * - Download LittleBigDataEvaluation.zip from piazza resources
 * - create a following folder structure
 * <p/>
 * Helloworld
 * |
 * |---resources
 * |--data_unittest
 * |        |---<all dat files>
 * |
 * |--expected
 * |        |---<all expected.dat files>
 * |
 * |--sqlFiles
 * |        |---<all sql files>
 * - might have to add junit to your path if it's not done by IDE automatically
 */

public class MainTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private final String LITTLE = "little";
    private final String MEDIUM = "medium";

    @Before
    public void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void checkpoint2_tpch07a_little() throws Exception {
        final String sqlFileName = "tpch07a";
        testLittleData(sqlFileName, LITTLE);
    }

    @Test
    public void checkpoint2_tpch10a_little() throws Exception {
        final String sqlFileName = "tpch10a";
        testLittleData(sqlFileName, LITTLE);
    }

    @Test
    public void checkpoint2_tpch12a_little() throws Exception {
        final String sqlFileName = "tpch12a";
        testLittleData(sqlFileName, LITTLE);
    }

    @Test
    public void checkpoint2_tpch16a_little() throws Exception {
        final String sqlFileName = "tpch16a";
        testLittleData(sqlFileName, LITTLE);
    }

    @Test
    public void testBuildPhase() throws Exception {
        String[] args = new String[]{"--data", "data_100mb", "sqlFiles/tpch_schemas.sql", "--swap", "swap", "--index", "index", "--build"};
        invokeTestClassWithArgs(args);
        assertEquals("", errContent.toString());
    }

    private void testLittleData(String sqlFileName, String size) throws IOException {
        String folderName;
        String[] args = new String[]{"--data", "resources/little/data_files", "resources/sql/" + sqlFileName + ".sql", "--swap", "resources/swap", "--index", "index"};
        folderName = "resources/little/expected/";

        invokeTestClassWithArgs(args);

        final String actualResult = outContent.toString().trim();

        String expectedData = getExpectedData(folderName, sqlFileName).trim();

//        assertEquals("", errContent.toString());
        assertEquals(expectedData, actualResult);
    }

    private void invokeTestClassWithArgs(String[] args) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.out), "ASCII"), 512);

        final Long start = System.currentTimeMillis();
        Main.main(args);
        final Long stop = System.currentTimeMillis();

        float diff = stop - start;
        float time = diff / (1000 * 60);
        out.write("Execution time :" + time + " minutes");
        out.write('\n');
        out.flush();
    }

    private String getExpectedData(String folderName, String sqlFileName) throws IOException {
        File file = new File(folderName + sqlFileName + ".expected.dat");
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        return new String(data, "UTF-8");
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(null);
        System.setErr(null);
    }
}
