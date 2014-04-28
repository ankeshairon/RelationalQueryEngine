package edu.buffalo.cse562.indexer.serializer;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;
import jdbm.Serializer;
import jdbm.SerializerInput;
import jdbm.SerializerOutput;

import java.io.IOException;

public class DatumArraySerializer implements Serializer<Datum[]> {
    private static final long serialVersionUID = -3818545055661017388L;

    private ColumnSchema[] rowSchema;
    private int tupleLength;

    public DatumArraySerializer(ColumnSchema[] rowSchema) {
        this.rowSchema = rowSchema;
        this.tupleLength = rowSchema.length;
    }

    public void serialize(SerializerOutput out, Datum[] tuple) throws IOException {
        int i = 0;
        try {
            for (; i < tuple.length; i++) {
                SerializerUtils.serializeDatum(out, tuple[i], rowSchema[i].getType());
            }
        } catch (Datum.CastException e) {
            throw new UnsupportedOperationException("Exception trying to cast " + tuple[i].toSTRING() + " as " + tuple[i].getType());
        }
    }

    public Datum[] deserialize(SerializerInput serializerInput) throws IOException {
        Datum[] tuple = new Datum[tupleLength];
        for (int i = 0; i < tupleLength; i++) {
            tuple[i] = SerializerUtils.deserializeDatum(serializerInput, rowSchema[i].getType());
        }
        return tuple;
    }
}
