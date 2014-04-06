package edu.buffalo.cse562.data;

public class FLOAT extends Datum {
    private Float f;

    public FLOAT(String s) {
        this.f = Float.parseFloat(s);
    }

    public FLOAT(float floatData) {
        this.f = floatData;
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
        return f;
    }

    @Override
    public type getType() {
        return Datum.type.FLOAT;
    }

    @Override
    public String toSTRING() {
        return Float.toString(f);
    }

    @Override
    public int compareTo(Object o) {
        return (f).compareTo(((FLOAT) o).f);
    }

    @Override
    public boolean equals(Object obj) {
        try {
            return (obj instanceof FLOAT) && f.equals(((FLOAT) obj).toFLOAT());
        } catch (CastException e) {
            return false;
        }

    }
}


