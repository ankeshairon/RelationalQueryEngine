package edu.buffalo.cse562.indexer.service;

import edu.buffalo.cse562.data.Datum;

import java.util.Set;

public interface IndexedDataMap {

    public Set<Datum> keySet();

    public Iterable<String> get(Datum key);
}
