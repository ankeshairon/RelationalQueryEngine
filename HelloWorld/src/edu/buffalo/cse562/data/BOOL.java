package edu.buffalo.cse562.data;

public class BOOL implements Datum {

    boolean b;

    public BOOL(String s) {
        this.b = Boolean.parseBoolean(s);
    }

    @Override
    public boolean toBOOL() throws CastException {
        return b;
    }

    @Override
    public long toLONG() throws CastException {
        throw new CastException();
    }

    @Override
    public float toFLOAT() throws CastException {
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
}
