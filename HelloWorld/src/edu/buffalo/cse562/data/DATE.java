package edu.buffalo.cse562.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DATE implements Datum {

    Date d;
    String s;

    public DATE(String s) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        this.d = df.parse(s);
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
        return Datum.type.DATE;
    }

    @Override
    public String toSTRING() {

        return this.toString();
    }

    @Override
    public int compareTo(Object datum) {
        return s.compareTo(((DATE) datum).s);
    }
}
