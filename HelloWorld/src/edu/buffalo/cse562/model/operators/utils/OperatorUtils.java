package edu.buffalo.cse562.model.operators.utils;

import java.util.ArrayList;
import java.util.List;

public class OperatorUtils {
    public static List<Integer> calculateIndicesOfTheseDataColumns(List<String> desiredColumnNames) {
        List<Integer> indices = new ArrayList<>();
        Integer indexOfColumn;
        for (String nameOfColumnToBeProjected : desiredColumnNames) {
            indexOfColumn = desiredColumnNames.indexOf(nameOfColumnToBeProjected);
            indices.add(indexOfColumn);
        }
        return indices;
    }
}
