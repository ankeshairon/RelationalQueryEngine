package edu.buffalo.cse562.data;

public abstract class Datum implements Comparable {

    public static enum type {
        BOOL, LONG, FLOAT, STRING, DATE
    }

    public static class CastException extends Exception {
        public CastException() {
            System.out.println("Cannot cast into type");
        }
    }

    public abstract String toSTRING();

    public abstract boolean toBOOL() throws CastException;

    public abstract Long toLONG() throws CastException;

    public abstract Float toFLOAT() throws CastException;

    public abstract type getType();
}
