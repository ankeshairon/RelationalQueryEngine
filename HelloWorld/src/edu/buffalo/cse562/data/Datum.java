package edu.buffalo.cse562.data;

import java.io.Externalizable;

public abstract class Datum implements Comparable<Datum>, Externalizable {

    public static enum type {
        BOOL, LONG, DOUBLE, STRING, DATE
    }

    public static class CastException extends Exception {
        public CastException() {
            System.out.println("Cannot cast into type");
        }
    }

    protected Datum() {
    }

    public abstract String toSTRING();

    public abstract boolean toBOOL() throws CastException;

    public abstract Long toLONG() throws CastException;

    public abstract Double toDOUBLE() throws CastException;

    public abstract type getType();

    public abstract int customHash(int hashFactor);
}
