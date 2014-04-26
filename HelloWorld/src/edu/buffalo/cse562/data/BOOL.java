package edu.buffalo.cse562.data;

public class BOOL extends Datum {

    private static final long serialVersionUID = -48805923424168509L;

    Boolean b;

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
    public Double toDOUBLE() throws CastException {
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
        return  b.compareTo(((BOOL) o).b);
    }

    @Override
    public boolean equals(Object obj) {
        try {
            return (obj instanceof BOOL) && b == (((BOOL) obj).toBOOL());
        } catch (CastException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return b.hashCode();
    }
}
