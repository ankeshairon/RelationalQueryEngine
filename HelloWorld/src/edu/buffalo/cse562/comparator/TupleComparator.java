package edu.buffalo.cse562.comparator;

import edu.buffalo.cse562.data.Datum;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class TupleComparator implements Comparator<Datum[]> {
    private Iterator<Integer> indexIterator;
    private int result;
    private Integer currentIndex;
    private LinkedHashMap<Integer, Boolean> indexesOfColumnsToSortOn;

    public TupleComparator(LinkedHashMap<Integer, Boolean> indexesOfColumnsToSortOn) {
        this.indexesOfColumnsToSortOn = indexesOfColumnsToSortOn;
        indexIterator = indexesOfColumnsToSortOn.keySet().iterator();
        result = 0;
    }

    @Override
    public int compare(Datum[] o1, Datum[] o2) {
        if (!indexIterator.hasNext()) {
            return result;
        }
        currentIndex = indexIterator.next();

        result = o1[currentIndex].compareTo(o2[currentIndex]);
        if (result == 0) {
            result = compare(o1, o2);
        } else {
            result = indexesOfColumnsToSortOn.get(currentIndex) ? result : -result;
        }
        indexIterator = indexesOfColumnsToSortOn.keySet().iterator();
        return result;
    }

}
