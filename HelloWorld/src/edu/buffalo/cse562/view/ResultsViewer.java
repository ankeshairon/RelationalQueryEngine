package edu.buffalo.cse562.view;

import edu.buffalo.cse562.model.data.ResultSet;

public class ResultsViewer {
    public static void viewResults(ResultSet resultSet) {
        while (resultSet.hasNext()) {
            System.out.println(resultSet.next());
        }
    }
}
