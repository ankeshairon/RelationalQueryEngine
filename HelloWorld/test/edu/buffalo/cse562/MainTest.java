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

    protected void invokeTestClassWithArgs(String[] args) throws IOException, InterruptedException {

        final Long start = System.currentTimeMillis();
        Main.main(args);
        final Long stop = System.currentTimeMillis();

        float diff = stop - start;
        float time = diff / (1000);

        print("Execution time :" + time + " seconds");
    }

    protected void print(String stringToPrint) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.out), "ASCII"), 512);
        out.write(stringToPrint);
        out.write('\n');
        out.flush();
    }
}
