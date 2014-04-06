package edu.buffalo.cse562;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

/**
 * Local test setup
 *
 * How to setup  -
 * - Download LittleBigDataEvaluation.zip from piazza resources
 * - create a following folder structure
 *
 *      Helloworld
 *          |
 *          |---resources
 *                  |--data_unittest
 *                  |        |---<all dat files>
 *                  |
 *                  |--expected
 *                  |        |---<all expected.dat files>
 *                  |
 *                  |--sqlFiles
 *                  |        |---<all sql files>
 * - might have to add junit to your path if it's not done by IDE automatically
 *
 * */

public class MainTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void checkpoint2() throws Exception {

        //Name of the query to run
        final String sqlFileName = "tpch16a";

        Main.main(new String[]{"--data", "resources/data_unittest", "resources/sql/" + sqlFileName + ".sql"});
        final String actualResult = outContent.toString().trim();

        String expectedData = getExpectedData(sqlFileName);

        assertEquals("", errContent.toString());
        assertEquals(expectedData, actualResult);
    }

    private String getExpectedData(String sqlFileName) throws IOException {
        File file = new File("resources/expected/" + sqlFileName + ".expected.dat");
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int)file.length()];
//        fis.read(data);
        fis.close();
        return new String(data, "UTF-8");
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(null);
        System.setErr(null);
    }
}
