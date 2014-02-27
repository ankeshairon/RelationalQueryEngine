package edu.buffalo.cse562.data;

public class FLOAT implements Datum {
    float f;

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
    public long toLONG() throws CastException {
        throw new CastException();
    }

    @Override
    public float toFLOAT() throws CastException {
        return f;
    }

    @Override
    public type getType() {
        return Datum.type.FLOAT;
    }

    @Override
    public String toSTRING() {
        // TODO Auto-generated method stub
        return Float.toString(f);
    }

    @Override
    public int compareTo(Object o) {
        return ((Float) f).compareTo(((FLOAT) o).f);

    }
}


