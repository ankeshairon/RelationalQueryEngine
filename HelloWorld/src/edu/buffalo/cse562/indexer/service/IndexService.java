package edu.buffalo.cse562.indexer.service;

import edu.buffalo.cse562.data.Datum;

import java.util.List;
import java.util.Map;

public interface IndexService {

    /**
     * Input param  tableName & name of indexed column
     * returns Map<Datum, List<<String>> where Datum is the key on which it is indexed & List<String> is the list of raw tuples
     */
    public Map<Datum, List<String>> getTuplesOfIndexesAsPer(String tableName, String columnName);

}
