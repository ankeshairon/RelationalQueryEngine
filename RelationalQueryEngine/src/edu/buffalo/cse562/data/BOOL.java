package edu.buffalo.cse562.data;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class BOOL extends Datum {

    private static final long serialVersionUID = -48805923424168509L;

    Boolean b;

    /**
     * only to be used for externalization. Do not use this
     */
    public BOOL() {
    }

    public BOOL(String s) {
        this.b = Boolean.parseBoolean(s);
    }

    public BOOL(Boolean b) {
        this.b = b;
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
    public int customHash(int hashFactor) {
        throw new UnsupportedOperationException("Custom hash not supported for BOOL");
    }

    @Override
    public String toSTRING() {
        return String.valueOf(b);
    }

    @Override
    public int compareTo(Datum o) {
        return b.compareTo(((BOOL) o).b);
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

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(b);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        b = in.readBoolean();
    }
}
