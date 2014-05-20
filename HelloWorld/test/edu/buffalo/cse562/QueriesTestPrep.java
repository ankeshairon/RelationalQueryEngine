package edu.buffalo.cse562;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

public class QueriesTestPrep extends MainTest {

    @Ignore
    @Test
    public void rebuildIndexesForCheckpoint4Queries() throws Exception {
        cleanDir(new File(QueriesTestCheckpoint4.indexDirPath));
        invokeTestClassWithArgs(getArgsForBuildPhaseOfCheckpoint4(QueriesTestCheckpoint4.dataDirPath, QueriesTestCheckpoint4.sqlDirPath), "index building");
    }

    @Ignore
    @Test
    public void rebuildIndexesForCheckpoint3Queries() throws Exception {
        cleanDir(new File(QueriesTestCheckpoint3.indexDirPath));
        invokeTestClassWithArgs(getArgsForBuildPhaseOfCheckpoint4(QueriesTestCheckpoint3.dataDirPath, QueriesTestCheckpoint3.sqlDirPath), "index building");
    }

    public void rebuildIndexes(String dataDirPath, String sqlDirPath, String indexDirPath) throws Exception {
        cleanDir(new File(indexDirPath));
        invokeTestClassWithArgs(getArgsForBuildPhaseOfCheckpoint4(dataDirPath, sqlDirPath), "index building");
    }

}
