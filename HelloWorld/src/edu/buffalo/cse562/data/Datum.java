package edu.buffalo.cse562.data;

import java.io.Serializable;

public abstract class Datum implements Comparable, Serializable {

    public static enum type {
        BOOL, LONG, DOUBLE, STRING, DATE
    }

    public static class CastException extends Exception {
        public CastException() {
            System.out.println("Cannot cast into type");
        }
    }

    public abstract String toSTRING();

    public abstract boolean toBOOL() throws CastException;

    public abstract Long toLONG() throws CastException;

    public abstract Double toDOUBLE() throws CastException;

    public abstract type getType();
}
