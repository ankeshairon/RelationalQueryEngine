package edu.buffalo.cse562;

import edu.buffalo.cse562.invoker.RelationalQueryEngine;

import javax.script.ScriptException;

public class Main {
    public static void main(String[] args) throws ScriptException {
        new RelationalQueryEngine().invoke(args);
    }
}
