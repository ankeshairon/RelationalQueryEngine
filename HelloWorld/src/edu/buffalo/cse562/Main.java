package edu.buffalo.cse562;

import edu.buffalo.cse562.invoker.RelationalQueryEngine;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        new RelationalQueryEngine().invoke(args);

        ArrayList l = new ArrayList();
        l.add(null);
        l.add(null);
        l.add("c");
        l.set(2, "b");
        l.set(1, "a");
    }
}
