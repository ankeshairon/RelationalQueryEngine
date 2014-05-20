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
        while (row != null) {
            StringBuilder result = new StringBuilder();
            for (Datum col : row) {
                result.append(DELIMITER).append(col.toSTRING());
            }

            System.out.println(result.substring(1, result.length()));
            row = source.readOneTuple();
        }

    }
}
