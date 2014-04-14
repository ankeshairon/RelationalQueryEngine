package edu.buffalo.cse562.data;

public class DatumUtilities {
    public static Datum getInstance(long l) {
        return new LONG(l);
    }

    public static Datum getInstance(Double f, Datum.type type) {
        return type == Datum.type.DOUBLE ? new DOUBLE(f) : new LONG(f.longValue());
    }
}
