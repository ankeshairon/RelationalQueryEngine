package edu.buffalo.cse562;

import org.junit.After;
import org.junit.Before;

import java.io.*;

public class MainTest {
    protected final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    protected final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    protected final String LITTLE = "little"; //8mb currently

    @Before
    public void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(null);
        System.setErr(null);
    }

    protected String invokeTestClassWithArgs(String[] args, String testFileName) throws IOException, InterruptedException {

        final Long start = System.currentTimeMillis();
        Main.main(args);
        final Long stop = System.currentTimeMillis();

        float diff = stop - start;
        float time = diff / (1000);

        return "Execution time for " + testFileName + " :" + time + " seconds";
    }

    protected void logTime(String log) throws IOException {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("TimeLog.txt", true)))) {
            out.println(log);
        } catch (IOException e) {
            print(e.getMessage());
        }
    }

    protected void print(String stringToPrint) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.out), "ASCII"), 512);
        out.write(stringToPrint);
        out.write('\n');
        out.flush();
    }

    protected String[] getArgsForBuildPhase(String dataFileDirPath, String sqlFileDirPath) {
        return new String[]{"--data", dataFileDirPath, "sqlFiles/tpch_schemas.sql",
                sqlFileDirPath + "/tpch1.sql",
                sqlFileDirPath + "/tpch3.sql",
                sqlFileDirPath + "/tpch5.sql",
                sqlFileDirPath + "/tpch6.sql",
                sqlFileDirPath + "/tpch07a.sql",
                sqlFileDirPath + "/tpch07b.sql",
                sqlFileDirPath + "/tpch07c.sql",
                sqlFileDirPath + "/tpch07d.sql",
                sqlFileDirPath + "/tpch07e.sql",
                sqlFileDirPath + "/tpch07f.sql",
                sqlFileDirPath + "/tpch07g.sql",
                sqlFileDirPath + "/tpch10a.sql",
                sqlFileDirPath + "/tpch10b.sql",
                sqlFileDirPath + "/tpch10c.sql",
                sqlFileDirPath + "/tpch10d.sql",
                sqlFileDirPath + "/tpch12a.sql",
                sqlFileDirPath + "/tpch12b.sql",
                sqlFileDirPath + "/tpch12c.sql",
                sqlFileDirPath + "/tpch12d.sql",
                sqlFileDirPath + "/tpch16a.sql",
                sqlFileDirPath + "/tpch16b.sql",
                sqlFileDirPath + "/tpch16c.sql",
                sqlFileDirPath + "/tpch16d.sql", "--swap", "swap", "--index", "index", "--build"};
    }
}
