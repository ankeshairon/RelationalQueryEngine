package edu.buffalo.cse562.comparator;

import edu.buffalo.cse562.data.Datum;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class TupleComparator implements Comparator<Datum[]> {
    private Iterator<Integer> indexIterator;
    private int result;
    private LinkedHashMap<Integer, Boolean> indexesOfColumnsToSortOn;

    /**
     * Takes as a constructor argument, a LinkedHashMap<Integer, Boolean>
     * where each entry is an index on which we need to sort and the boolean is to specify Asc or Desc
     * <p/>
     * e.g. to sort on index 1 in Asc order & in index 3 in desc order, the input map will
     * have entries like
     * {
     * (1, true),
     * (3, false)
     * }
     */
    public TupleComparator(LinkedHashMap<Integer, Boolean> indexesOfColumnsToSortOn) {
        this.indexesOfColumnsToSortOn = indexesOfColumnsToSortOn;
        indexIterator = indexesOfColumnsToSortOn.keySet().iterator();
    }

    @Override
    public int compare(Datum[] o1, Datum[] o2) {
        result = 0;
        return compareThese(o1, o2);
    }

    private int compareThese(Datum[] o1, Datum[] o2) {
        if (!indexIterator.hasNext()) {
            return result;
        }
        int currentIndex = indexIterator.next();

        result = o1[currentIndex].compareTo(o2[currentIndex]);
        if (result == 0) {
            result = compareThese(o1, o2);
        } else {
            result = indexesOfColumnsToSortOn.get(currentIndex) ? result : -result;
        }
        indexIterator = indexesOfColumnsToSortOn.keySet().iterator();
        return result;
    }

}
