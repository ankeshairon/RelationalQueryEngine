package edu.buffalo.cse562.data;

public class STRING extends Datum {

    String s;

    public STRING(String s) {
        this.s = s;
    }

    @Override
    public boolean toBOOL() throws CastException {
        throw new CastException();
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
        return Datum.type.STRING;
    }

    @Override
    public String toSTRING() {
        return s;
    }

    @Override
    public int compareTo(Object o) {
        return s.compareTo(((STRING) o).s);
    }
}
