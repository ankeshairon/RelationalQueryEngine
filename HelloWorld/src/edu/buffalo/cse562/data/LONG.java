package edu.buffalo.cse562.data;

public class LONG implements Datum {

    long l;

    public LONG(String s) {
        this.l = Long.parseLong(s);
    }

    @Override
    public boolean toBOOL() throws CastException {
        throw new CastException();
    }

    @Override
    public long toLONG() throws CastException {
        return l;
    }

    @Override
    public float toFLOAT() throws CastException {
        return (float) l;
    }

    @Override
    public type getType() {
        return Datum.type.LONG;
    }

    @Override
    public String toSTRING() {
        return null;
    }

    @Override
    public int compareTo(Object o) {
        return ((Long) l).compareTo(((LONG) o).l);
    }
}
