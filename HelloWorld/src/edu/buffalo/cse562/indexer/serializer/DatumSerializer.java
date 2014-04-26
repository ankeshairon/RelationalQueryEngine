package edu.buffalo.cse562.indexer.serializer;

import edu.buffalo.cse562.data.Datum;
import jdbm.Serializer;
import jdbm.SerializerInput;
import jdbm.SerializerOutput;

import java.io.IOException;

public class DatumSerializer implements Serializer<Datum> {
    private static final long serialVersionUID = -3818545055661017388L;

    public static final DatumSerializer INSTANCE = new DatumSerializer();

    /**
     * Construct a DefaultSerializer, is private to make sure every one uses INSTANCE
     */
    private DatumSerializer() {
        // no op
    }

    public void serialize(SerializerOutput out, Datum datum) throws IOException {
        out.writeObject(datum);
    }

    public Datum deserialize(SerializerInput serializerInput) throws IOException {
        try {
            return (Datum) serializerInput.readObject();
        } catch (ClassNotFoundException except) {
            throw new IOException(except);
        }
    }
}
