package edu.buffalo.cse562;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.operator.Operator;

public class View {

    private static final String DELIMITER = "|";

    public static void dump(Operator source) {
        if (source == null) {
            return;
        }

        Datum[] row = source.readOneTuple();
        StringBuilder collectedTuples;
        StringBuilder tupleBuilder;
//        int counter = 0;

//        while (row != null) {
        collectedTuples = new StringBuilder();

//            while (row != null && counter != 1000) {
        while (row != null) {
            tupleBuilder = new StringBuilder();
            for (Datum col : row) {
                tupleBuilder.append(DELIMITER).append(col.toSTRING());
            }
            collectedTuples.append("\n").append(tupleBuilder.substring(1));
            row = source.readOneTuple();
//                ++counter;
//            }
//            System.out.println(collectedTuples.toString());
//            counter = 0;
        }
        System.out.println(collectedTuples.substring(1));
    }
}
