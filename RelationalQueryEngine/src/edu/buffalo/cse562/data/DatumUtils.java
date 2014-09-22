package edu.buffalo.cse562.data;

public class DatumUtils {
    public static Datum getInstance(long l) {
        return new LONG(l);
    }

    public static Datum getInstance(Double f, Datum.type type) {
        return type == Datum.type.DOUBLE ? new DOUBLE(f) : new LONG(f.longValue());
    }

    public static Datum getDatumOfTypeFromValue(Datum.type datumType, String rawDatumValue) {
        Datum datum;
        switch (datumType) {
            case LONG:
                datum = new LONG(rawDatumValue);
                break;
            case DOUBLE:
                datum = new DOUBLE(rawDatumValue);
                break;
            case BOOL:
                datum = new BOOL(rawDatumValue);
                break;
            case DATE:
//                    try {
//                        tuple[i] = new DATE(cells[index]);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    break;
            case STRING:
                datum = new STRING(rawDatumValue);
                break;
            default:
                throw new UnsupportedOperationException("Datum type not supported" + datumType);
        }
        return datum;
    }
}
