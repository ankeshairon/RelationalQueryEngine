package edu.buffalo.cse562;

import org.junit.Test;

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
 * |        |---sql_buildphase
 * |        |        |---<all sql query files>
 * |---swap
 * |---<empty directory>
 * - might have to add junit to your path if it's not done by IDE automatically
 */
public class BuildPhaseTest extends MainTest {

    @Test
    public void testBuildPhaseFor10KB() throws Exception {
        testBuildPhaseWithDataFile("data_10kb", "indexes building for 10kb");
    }

    @Test
    public void testBuildPhaseFor1MB() throws Exception {
        testBuildPhaseWithDataFile("data_1mb", "indexes building for 1mb");
    }

    @Test
    public void testBuildPhaseFor8MB() throws Exception {
        testBuildPhaseWithDataFile("resources/little/data_files", "indexes building for 8mb");
    }

    @Test
    public void testBuildPhaseFor40MB() throws Exception {
        testBuildPhaseWithDataFile("resources/normal/data_files", "indexes building for 40mb");
    }

    @Test
    public void testBuildPhaseFor100MB() throws Exception {
        testBuildPhaseWithDataFile("resources/large/data_files", "indexes building for 100mb");
    }

    private void testBuildPhaseWithDataFile(String dataFileDirPath, String testName) throws IOException, InterruptedException {
        String[] args = getArgsForBuildPhaseOfCheckpoint3(dataFileDirPath, "resources/sql_buildphase");
        invokeTestClassWithArgs(args, testName);
        assertEquals("", errContent.toString());
    }
}
