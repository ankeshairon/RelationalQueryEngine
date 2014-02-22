package edu.buffalo.cse562.view;

import edu.buffalo.cse562.model.data.ResultSet;
import edu.buffalo.cse562.model.data.Tuple;

import java.util.ListIterator;

public class ResultsViewer {
    public static void viewResults(ResultSet resultSet) {
        ListIterator<Tuple> iterator = resultSet.getTuplesListIteratorFromFirstElement();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}
