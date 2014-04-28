package edu.buffalo.cse562.indexer.serializer;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;
import jdbm.Serializer;
import jdbm.SerializerInput;
import jdbm.SerializerOutput;

import java.io.IOException;

public class DatumSerializer implements Serializer<Datum> {
    private static final long serialVersionUID = -3818545055661017388L;
    private ColumnSchema schema;

    public DatumSerializer(ColumnSchema schema) {
        this.schema = schema;
    }

    public void serialize(SerializerOutput out, Datum datum) throws IOException {
        try {
            SerializerUtils.serializeDatum(out, datum, schema.getType());
        } catch (Datum.CastException e) {
            throw new UnsupportedOperationException("Exception trying to cast " + datum.toSTRING() + " as " + datum.getType());
        }
    }

    public Datum deserialize(SerializerInput serializerInput) throws IOException {
        return SerializerUtils.deserializeDatum(serializerInput, schema.getType());
    }
}
