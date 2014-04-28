package edu.buffalo.cse562.comparator;

import edu.buffalo.cse562.data.Datum;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SerializableTupleComparator implements Comparator<Datum[]>, Externalizable {
    private List<Integer> indexesOfColumnsToSortOn;
    private int result;
    private Integer i;

    private static final long serialVersionUID = -488059234241685509L;


    /**
     * Takes as a constructor argument, a List<Integer>
     * where each entry is an i on which we need to sort on Asc
     * <p/>
     * e.g. to sort on indexes 1,3, the input list will
     * have entries like {1,3}
     */
    public SerializableTupleComparator(List<Integer> indexesOfColumnsToSortOn) {
        this.indexesOfColumnsToSortOn = indexesOfColumnsToSortOn;
        i = 0;
    }

    /**
     * only to be used for externalization. Do not use this
     */
    public SerializableTupleComparator() {
    }

    @Override
    public int compare(Datum[] o1, Datum[] o2) {
        result = 0;
        i = 0;
        return compareThese(o1, o2, i);
    }

    private int compareThese(Datum[] o1, Datum[] o2, Integer index) {
        if (indexesOfColumnsToSortOn.size() <= index) {
            return result;
        }

        final Integer indexToSortOn = indexesOfColumnsToSortOn.get(index);

        try {
            result = (o1[indexToSortOn]).compareTo(o2[indexToSortOn]);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        if (result == 0) {
            result = compareThese(o1, o2, ++index);
        }
        return result;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(indexesOfColumnsToSortOn.size());
        for (Integer index : indexesOfColumnsToSortOn) {
            out.writeInt(index);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        indexesOfColumnsToSortOn = new ArrayList<>();
        int size = in.readInt();

        for (int j = 0; j < size; j++) {
            indexesOfColumnsToSortOn.add(in.readInt());
        }
    }
}
