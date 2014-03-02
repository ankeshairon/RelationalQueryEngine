package edu.buffalo.cse562;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.operator.Operator;

import java.text.DecimalFormat;

public class View {
    public static void dump(Operator source) {
        if (source == null) {
            return;
        }

        Datum[] row = source.readOneTuple();
        while (row != null) {
            StringBuilder result = new StringBuilder();
            for (Datum col : row) {
                switch (col.getType()) {
                    case LONG:
                        try {
                            long l = col.toLONG();
                            result.append(l).append("|");
                        } catch (Datum.CastException e) {
                            e.printStackTrace();
                        }
                        break;
                    case STRING:
                        String s = col.toSTRING();
                        result.append(s).append("|");

                        break;
                    case FLOAT:
                        try {
                            result.append(new DecimalFormat("#.########").format(col.toFLOAT())).append("|");
                        } catch (Datum.CastException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }

            System.out.println(result.substring(0, result.length() - 1));
            row = source.readOneTuple();
        }

    }
}
