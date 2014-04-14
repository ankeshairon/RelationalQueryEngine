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

    @Before
    public void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void checkpoint2_tpch07a() throws Exception {
        final String sqlFileName = "tpch07a";
        testQuery(sqlFileName);
    }

    @Test
    public void checkpoint2_tpch10a() throws Exception {
        final String sqlFileName = "tpch10a";
        testQuery(sqlFileName);
    }

    @Test
    public void checkpoint2_tpch12a() throws Exception {
        final String sqlFileName = "tpch12a";
        testQuery(sqlFileName);
    }

    @Test
    public void checkpoint2_tpch16a() throws Exception {
        final String sqlFileName = "tpch16a";
        testQuery(sqlFileName);
    }

    private void testQuery(String sqlFileName) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.out), "ASCII"), 512);
        final Long start = System.currentTimeMillis();

        Main.main(new String[]{"--data", "resources/data_unittest", "resources/sql/" + sqlFileName + ".sql", "--swap", "resources/swap"});

        final Long stop = System.currentTimeMillis();
        float diff = stop - start;
        float time =  diff/ (1000 * 60);
        out.write("Execution time :" + time + " minutes");
        out.write('\n');
        out.flush();

        final String actualResult = outContent.toString().trim();

        String expectedData = getExpectedData(sqlFileName).trim();

//        assertEquals("", errContent.toString());
        assertEquals(expectedData, actualResult);
    }

    private String getExpectedData(String sqlFileName) throws IOException {
        File file = new File("resources/expected/" + sqlFileName + ".expected.dat");
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
