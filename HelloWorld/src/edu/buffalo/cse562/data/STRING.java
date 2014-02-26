package edu.buffalo.cse562.data;

public class STRING implements Datum {

    String s;

    public STRING(String s) {
        this.s = s;
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
	public int compareTo(Datum datum) throws CastException {
		// TODO Auto-generated method stub
		return this.toSTRING().compareTo(datum.toSTRING());
	}

}
