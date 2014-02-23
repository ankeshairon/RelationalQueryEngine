package edu.buffalo.cse562.data;

public interface Datum {

    static enum type {
        BOOL, LONG, FLOAT, STRING, DATE
    }

    ;

    static class CastException extends Exception {
        public CastException() {
            System.out.println("Cannot cast into type");
        }
    }

    String toSTRING();

    boolean toBOOL() throws CastException;

    long toLONG() throws CastException;

    float toFLOAT() throws CastException;

    type getType();

    //Datum.type getValue();

}
