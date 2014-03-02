package edu.buffalo.cse562.data;

public class BOOL extends Datum {

    boolean b;

    public BOOL(String s) {
        this.b = Boolean.parseBoolean(s);
    }

    @Override
    public boolean toBOOL() throws CastException {
        return b;
    }

    @Override
    public Long toLONG() throws CastException {
        throw new CastException();
    }

    @Override
    public Float toFLOAT() throws CastException {
        throw new CastException();
    }

    @Override
    public type getType() {
        return Datum.type.BOOL;
    }

    @Override
    public String toSTRING() {
        return String.valueOf(b);
    }

    @Override
    public int compareTo(Object o) {
        return ((Boolean) b).compareTo(((BOOL) o).b);
    }

//    @Override
//    public Number getNumber() {
//        throw new ClassCastException("Cannot convert boolean to number");
//    }
//
//    @Override
//    public Datum multiply(Datum that) {
//        return null;
//    }
}
