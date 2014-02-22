package edu.buffalo.cse562;

import edu.buffalo.cse562.invoker.RelationalQueryEngine;

public class Main {
    public static void main(String[] args) {
        new RelationalQueryEngine().invoke(args);

//        Map<Integer, Integer> map = new HashMap<>();
//        map.put(0,0);
//        map.put(0, map.get(0) + 1);
//        System.out.println(map.get(0));

    }
}
