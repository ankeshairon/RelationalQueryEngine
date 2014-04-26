package edu.buffalo.cse562.data;

import java.text.DecimalFormat;

public class DOUBLE extends Datum {
    private static final long serialVersionUID = -4880923424168509L;

    static DecimalFormat decimalFormat = new DecimalFormat("#.####");
    private Double d;

    public DOUBLE(String s) {
        this.d = Double.parseDouble(s);
    }

    public DOUBLE(Double floatData) {
        this.d = floatData;
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
    public Double toDOUBLE() throws CastException {
        return d;
    }

    @Override
    public type getType() {
        return Datum.type.DOUBLE;
    }

    @Override
    public String toSTRING() {
        return decimalFormat.format(d);
    }

    @Override
    public int compareTo(Object o) {
        return (d).compareTo(((DOUBLE) o).d);
    }

    @Override
    public boolean equals(Object obj) {
        try {
            return (obj instanceof DOUBLE) && d.equals(((DOUBLE) obj).toDOUBLE());
        } catch (CastException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return d.hashCode();
    }
}


