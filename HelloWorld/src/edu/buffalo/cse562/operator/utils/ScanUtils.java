package edu.buffalo.cse562.operator.utils;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;

import java.util.List;

import static edu.buffalo.cse562.data.DatumUtils.getDatumOfTypeFromValue;

public class ScanUtils {
    public static Datum[] getDatumsForRelevantColumnPositions(String line, List<Integer> relevantColumnIndexes, ColumnSchema[] schema) {
        String[] cells = line.split("\\|");
        Datum[] tuple = new Datum[relevantColumnIndexes.size()];

        for (int i = 0; i < relevantColumnIndexes.size(); i++) {
            Integer index = relevantColumnIndexes.get(i);

            tuple[i] = getDatumOfTypeFromValue(schema[i].getType(), cells[index]);
        }
        return tuple;
    }
}
