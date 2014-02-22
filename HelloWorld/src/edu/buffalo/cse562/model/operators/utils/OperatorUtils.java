package edu.buffalo.cse562.model.operators.utils;

import java.util.ArrayList;
import java.util.List;

public class OperatorUtils {
    public static List<Integer> calculateIndicesOfTheseDataColumns(List<String> superset, String... subset) {
        List<Integer> indices = new ArrayList<>();
        Integer indexOfColumn;
        for (String nameOfColumnToBeProjected : subset) {
            indexOfColumn = superset.indexOf(nameOfColumnToBeProjected);
            indices.add(indexOfColumn);
        }
        return indices;
    }
}
