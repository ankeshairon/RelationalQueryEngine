package edu.buffalo.cse562.data;

import edu.buffalo.cse562.data.Datum.CastException;

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
	public int compareTo(Datum datum) throws CastException {
		int comp;
		if (this.toFLOAT() == datum.toFLOAT())
			comp = 0;
		else if (this.toFLOAT() > datum.toFLOAT())
			comp = 1;
		else 
			comp = -1;
		return comp;
	}
}


