package edu.buffalo.cse562.data;

public class LONG extends Datum {

    Long l;

    public LONG(String s) {
        this.l = Long.parseLong(s);
    }

    public LONG(int i) {
        l = (long)i ;
    }

    public LONG(Long l) {
        this.l = l;
    }

    @Override
    public boolean toBOOL() throws CastException {
        throw new CastException();
    }

    @Override
    public Long toLONG() throws CastException {
        return l;
    }

    @Override
    public Float toFLOAT() throws CastException {
        return (float) l;
    }

    @Override
    public type getType() {
        return Datum.type.LONG;
    }

    @Override
    public String toSTRING() {
        return Long.toString(l);
    }

    @Override
    public int compareTo(Object o) {
        return (l).compareTo(((LONG) o).l);
    }

    @Override
    public boolean equals(Object obj) {
        try {
            return (obj instanceof LONG) && l.equals(((LONG) obj).toLONG());
        } catch (CastException e) {
            return false;
        }
    }
}
