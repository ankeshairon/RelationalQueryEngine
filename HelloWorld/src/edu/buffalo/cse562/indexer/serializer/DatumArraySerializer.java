package edu.buffalo.cse562.indexer.serializer;

import edu.buffalo.cse562.data.Datum;
import jdbm.Serializer;
import jdbm.SerializerInput;
import jdbm.SerializerOutput;

import java.io.IOException;
import java.util.Arrays;

public class DatumArraySerializer implements Serializer<Datum[]> {
    private static final long serialVersionUID = -3818545055661017388L;

    public DatumArraySerializer() {
        //todo add Datum Serializer to this
    }

    public void serialize(SerializerOutput out, Datum[] datum) throws IOException {
        out.writeObject(datum);
    }

    public Datum[] deserialize(SerializerInput serializerInput) throws IOException {
        try {
            final Object[] objects = serializerInput.readObject();
            return Arrays.copyOf(objects, objects.length, Datum[].class);
        } catch (ClassNotFoundException except) {
            throw new IOException(except);
        }
    }
}
