package edu.buffalo.cse562.indexer.serializer;

import edu.buffalo.cse562.data.Datum;
import jdbm.Serializer;
import jdbm.SerializerInput;
import jdbm.SerializerOutput;

import java.io.IOException;
import java.util.Arrays;

public class DatumArraySerializer implements Serializer<Datum[]> {
    private static final long serialVersionUID = -3818545055661017388L;

    public static final DatumArraySerializer INSTANCE = new DatumArraySerializer();


    /**
     * Construct a DefaultSerializer, is private to make sure every one uses INSTANCE
     */
    private DatumArraySerializer() {
        // no op
    }


    /**
     * Serialize the content of an object into a byte array.
     *
     * @param datum array Object to serialize
     * @return a byte array representing the object's state
     */
    public void serialize(SerializerOutput out, Datum[] datum) throws IOException {
        out.writeObject(datum);
    }


    /**
     * Deserialize the content of an object from a byte array.
     *
     * @param serializerInput serialized Byte array representation of the Datum
     * @return deserialized Datum
     */
    public Datum[] deserialize(SerializerInput serializerInput) throws IOException {
        try {
            final Object[] objects = serializerInput.readObject();
            return Arrays.copyOf(objects, objects.length, Datum[].class);
        } catch (ClassNotFoundException except) {
            throw new IOException(except);
        }
    }
}
