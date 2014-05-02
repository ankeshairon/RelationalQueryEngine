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
 * |---index
 * |        |---<should be emptied after every run>     //todo clear indexes after every run
 * |---resources
 * |        |---little      (for 8mb data)
 * |        |         |---data_files
 * |        |         |         |---<all dat files>
 * |         |--expected
 * |        |                   |---<all expected.dat files>
 * |        |---medium      (for 40mb data)
 * |        |        |---data_files
 * |        |        |          |---<all dat files>
 * |        |--expected
 * |        |                   |---<all expected.dat files>
 * |        |---sqlFiles
 * |        |        |---<all sql query files>
 * |---swap
 * |---<empty directory>
 * - might have to add junit to your path if it's not done by IDE automatically
 */

public class MainTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private final String LITTLE = "little"; //8mb currently

    @Before
    public void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void checkpoint2_tpch07a_little() throws Exception {
        testForExpectedData("tpch07a", LITTLE);
    }

    @Test
    public void checkpoint2_tpch10a_little() throws Exception {
        testForExpectedData("tpch10a", LITTLE);
    }

    @Test
    public void checkpoint2_tpch12a_little() throws Exception {
        testForExpectedData("tpch12a", LITTLE);
    }

    @Test
    public void checkpoint2_tpch16a_little() throws Exception {
        testForExpectedData("tpch16a", LITTLE);
    }

    @Test
    public void testBuildPhaseFor10KB() throws Exception {
        testBuildPhaseWithDataFile("data_10kb");
    }

    @Test
    public void testBuildPhaseFor1MB() throws Exception {
        testBuildPhaseWithDataFile("data_1mb");
    }

    @Test
    public void testBuildPhaseFor8MB() throws Exception {
        testBuildPhaseWithDataFile("resources/little/data_files");
    }

    @Test
    public void testBuildPhaseFor40MB() throws Exception {
        testBuildPhaseWithDataFile("resources/normal/data_files");
    }

    @Test
    public void testBuildPhaseFor100MB() throws Exception {
        testBuildPhaseWithDataFile("resources/large/data_files");
    }

    private void testBuildPhaseWithDataFile(String dataFileArg) throws IOException, InterruptedException {
        String[] args = getArgsForBuildPhase(dataFileArg);
        invokeTestClassWithArgs(args);
        assertEquals("", errContent.toString());
    }

    private void testForExpectedData(String sqlFileName, String size) throws IOException, InterruptedException {
        String folderName;
        String[] args = new String[]{"--data", "resources/little/data_files", "sqlFiles/tpch_schemas.sql", "resources/sql/" + sqlFileName + ".sql", "--swap", "resources/swap", "--index", "index"};
        folderName = "resources/little/expected/";

        invokeTestClassWithArgs(args);

        final String actualResult = outContent.toString().trim();

        String expectedData = getExpectedData(folderName, sqlFileName).trim();

//        assertEquals("", errContent.toString());
        assertEquals(expectedData, actualResult);
    }

    private void invokeTestClassWithArgs(String[] args) throws IOException, InterruptedException {

        final Long start = System.currentTimeMillis();
        Main.main(args);
        final Long stop = System.currentTimeMillis();

        float diff = stop - start;
        float time = diff / (1000);

        print("Execution time :" + time + " seconds");
    }

    private void print(String stringToPrint) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.out), "ASCII"), 512);
        out.write(stringToPrint);
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

    private String[] getArgsForBuildPhase(String dataFileArg) {
        return new String[]{"--data", dataFileArg, "sqlFiles/tpch_schemas.sql",
                "resources/sql/tpch1.sql",
                "resources/sql/tpch3.sql",
                "resources/sql/tpch5.sql",
                "resources/sql/tpch6.sql",
                "resources/sql/tpch07a.sql",
                "resources/sql/tpch07b.sql",
                "resources/sql/tpch07c.sql",
                "resources/sql/tpch07d.sql",
                "resources/sql/tpch07e.sql",
                "resources/sql/tpch07f.sql",
                "resources/sql/tpch07g.sql",
                "resources/sql/tpch10a.sql",
                "resources/sql/tpch10b.sql",
                "resources/sql/tpch10c.sql",
                "resources/sql/tpch10d.sql",
                "resources/sql/tpch12a.sql",
                "resources/sql/tpch12b.sql",
                "resources/sql/tpch12c.sql",
                "resources/sql/tpch12d.sql",
                "resources/sql/tpch16a.sql",
                "resources/sql/tpch16b.sql",
                "resources/sql/tpch16c.sql",
                "resources/sql/tpch16d.sql", "--swap", "swap", "--index", "index", "--build"};
    }
}
