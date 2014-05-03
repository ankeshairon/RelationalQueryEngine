package edu.buffalo.cse562;

import org.junit.Test;

public class QueriesTestPrep extends MainTest {
    @Test
    public void buildIndexesOnceBeforeTestingQueries() throws Exception {
        invokeTestClassWithArgs(getArgsForBuildPhase(QueriesTest.dataDirPath, QueriesTest.sqlDirPath), "index building");
    }
}
