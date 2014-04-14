package edu.buffalo.cse562.data;

import java.text.DecimalFormat;

public class FLOAT extends Datum {
    DecimalFormat decimalFormat = new DecimalFormat("#.####");
    private Double f;

    public FLOAT(String s) {
        this.f = Double.parseDouble(s);
    }

    public FLOAT(Double floatData) {
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
    public Double toFLOAT() throws CastException {
        return f;
    }

    @Override
    public type getType() {
        return Datum.type.FLOAT;
    }

    @Override
    public String toSTRING() {
        return decimalFormat.format(f);
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

    @Override
    public int hashCode() {
        return f.hashCode();
    }
}


