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

    private String[] getArgsForBuildPhase(String dataFileArg) {
        return new String[]{"--data", dataFileArg, "sqlFiles/tpch_schemas.sql",
                "resources/sql_buildphase/tpch1.sql",
                "resources/sql_buildphase/tpch3.sql",
                "resources/sql_buildphase/tpch5.sql",
                "resources/sql_buildphase/tpch6.sql",
                "resources/sql_buildphase/tpch07a.sql",
                "resources/sql_buildphase/tpch07b.sql",
                "resources/sql_buildphase/tpch07c.sql",
                "resources/sql_buildphase/tpch07d.sql",
                "resources/sql_buildphase/tpch07e.sql",
                "resources/sql_buildphase/tpch07f.sql",
                "resources/sql_buildphase/tpch07g.sql",
                "resources/sql_buildphase/tpch10a.sql",
                "resources/sql_buildphase/tpch10b.sql",
                "resources/sql_buildphase/tpch10c.sql",
                "resources/sql_buildphase/tpch10d.sql",
                "resources/sql_buildphase/tpch12a.sql",
                "resources/sql_buildphase/tpch12b.sql",
                "resources/sql_buildphase/tpch12c.sql",
                "resources/sql_buildphase/tpch12d.sql",
                "resources/sql_buildphase/tpch16a.sql",
                "resources/sql_buildphase/tpch16b.sql",
                "resources/sql_buildphase/tpch16c.sql",
                "resources/sql_buildphase/tpch16d.sql", "--swap", "swap", "--index", "index", "--build"};
    }
}
