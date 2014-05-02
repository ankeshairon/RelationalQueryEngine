package edu.buffalo.cse562.indexer.service;

import edu.buffalo.cse562.data.Datum;
import jdbm.SecondaryTreeMap;

import java.util.ArrayList;
import java.util.List;

public class IndexedDataMap {

    private final SecondaryTreeMap<Datum, Long, String> secondaryMap;

    public IndexedDataMap(SecondaryTreeMap<Datum, Long, String> secondaryMap) {
        this.secondaryMap = secondaryMap;
    }

    public List<Long> getRowIdsForKey(Datum key) {
        return (List<Long>) secondaryMap.get(key);
    }

    public List<String> getTuplesForIds(List<Long> ids) {
        List<String> tuples = new ArrayList<>();
        for (Long id : ids) {
            tuples.add(getTupleForId(id));
        }
        return tuples;
    }

    public String getTupleForId(Long id) {
        return secondaryMap.getPrimaryValue(id);
    }

}
