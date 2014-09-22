package edu.buffalo.cse562.indexer.serializer;

import edu.buffalo.cse562.data.*;
import jdbm.SerializerInput;
import jdbm.SerializerOutput;

import java.io.IOException;

public class SerializerUtils {
    public static void serializeDatum(SerializerOutput out, Datum datum, Datum.type datumType) throws IOException, Datum.CastException {
        switch (datumType) {
            case BOOL:
                out.writeBoolean(datum.toBOOL());
                break;
            case LONG:
                out.writeLong(datum.toLONG());
                break;
            case DOUBLE:
                out.writeDouble(datum.toDOUBLE());
                break;
            case DATE:
            case STRING:
                out.writeUTF(datum.toSTRING());
                break;
            default:
                throw new UnsupportedOperationException("Unknown data type. Don't know how to serialize");
        }
    }

    public static Datum deserializeDatum(SerializerInput serializerInput, Datum.type datumType) throws IOException {
        switch (datumType) {
            case BOOL:
                return new BOOL(serializerInput.readBoolean());
            case LONG:
                return new LONG(serializerInput.readLong());
            case DOUBLE:
                return new DOUBLE(serializerInput.readDouble());
            case DATE:
            case STRING:
                return new STRING(serializerInput.readUTF());
            default:
                throw new UnsupportedOperationException("Unknown data type. Don't know how to deserialize " + datumType);
        }
    }
}
