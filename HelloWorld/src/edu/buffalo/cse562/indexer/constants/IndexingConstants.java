package edu.buffalo.cse562.indexer.constants;

import java.util.Arrays;
import java.util.List;

public class IndexingConstants {
    public static final String INDEX_KEY = "INDEX";
    public static final String PRIMARY_KEY = "PRIMARY KEY";
    //    public static final String INDEX_DATABASE_NAME = "RQE";
    public static final String DEFAULT_PRIMARY_INDEX_NAME = "PRIMARY_INDEX";

    public static List<String> queries = Arrays.asList(
            "tpch1.sql",
            "tpch3.sql",
            "tpch5.sql",
            "tpch6.sql",
            "tpch07a.sql",
            "tpch10a.sql",
            "tpch12a.sql",
            "tpch16a.sql");
}
