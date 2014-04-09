package edu.buffalo.cse562.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DATE extends Datum {

//    Date d;
    String s;

    public DATE(String s) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        this.d = df.parse(s);
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
    public Float toFLOAT() throws CastException {
        throw new CastException();
    }

    @Override
    public type getType() {
        return Datum.type.DATE;
    }

    @Override
    public String toSTRING() {
        return s;
    }

    @Override
    public int compareTo(Object datum) {
        return s.compareTo(((DATE) datum).s);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof DATE) && s.equals(((DATE) obj).toSTRING());
    }

    @Override
    public int hashCode() {
        return s.hashCode();
    }
}
