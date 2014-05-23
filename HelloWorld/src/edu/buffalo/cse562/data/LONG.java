package edu.buffalo.cse562.data;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class LONG extends Datum {
    private static final long serialVersionUID = -4880592342168509L;

    Long l;

    /**
     * only to be used for externalization. Do not use this
     */
    public LONG() {
    }

    public LONG(String s) {
        this.l = Long.parseLong(s);
    }

    public LONG(int i) {
        l = (long) i;
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
        return l.longValue();
    }

    @Override
    public Double toDOUBLE() throws CastException {
        return (double) l;
    }

    @Override
    public type getType() {
        return Datum.type.LONG;
    }

    @Override
    public int customHash(int hashFactor) {
        return l.intValue() % hashFactor;
    }

    @Override
    public String toSTRING() {
        return Long.toString(l);
    }

    @Override
    public int compareTo(Datum o) {
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

    @Override
    public int hashCode() {
        return l.hashCode();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(l);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        l = in.readLong();
    }
}
