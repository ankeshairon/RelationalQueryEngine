package edu.buffalo.cse562.data;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class STRING extends Datum {
    private static final long serialVersionUID = -48805923424168539L;

    String s;

    /**
     * only to be used for externalization. Do not use this
     */
    public STRING() {
    }

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
    public Double toDOUBLE() throws CastException {
        throw new CastException();
    }

    @Override
    public type getType() {
        return Datum.type.STRING;
    }

    @Override
    public int customHash(int hashFactor) {
        return s.length() % hashFactor;
    }

    @Override
    public String toSTRING() {
        return s;
    }

    @Override
    public int compareTo(Datum o) {
        return s.compareTo(((STRING) o).s);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof STRING) && s.equals(((STRING) obj).toSTRING());
    }

    @Override
    public int hashCode() {
        return s.hashCode();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(s);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        s = in.readUTF();
    }
}
