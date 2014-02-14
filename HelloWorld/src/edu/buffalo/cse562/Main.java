package edu.buffalo.cse562;

import edu.buffalo.cse562.invoker.RelationalQueryEngine;

public class Main {
    public static void main(String[] args) {
        System.out.println("We, the members of our team, agree that we will not submit any code that we have not written ourselves, share our code with anyone outside of our group, or use code that we have not written ourselves as a reference.");

        //TODO may be add args check for --data. Have posted a question on piazza regarding this https://piazza.com/class/ho3bfjiiv3b1by?cid=88
        new RelationalQueryEngine(args[1], args[2]).invoke();
    }
}
