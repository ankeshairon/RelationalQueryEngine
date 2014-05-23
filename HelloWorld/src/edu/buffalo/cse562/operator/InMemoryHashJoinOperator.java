package edu.buffalo.cse562.operator;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.data.Datum.CastException;
import edu.buffalo.cse562.schema.ColumnSchema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class InMemoryHashJoinOperator extends JoinOperator {

    int indexR, indexS;
    ColumnSchema[] outputSchema;

    ArrayList<Datum[]> result;
    Iterator<Datum[]> iter;

    public InMemoryHashJoinOperator(Operator R, Operator S, int indexR, int indexS) {
        super(R, S);
        this.indexR = indexR;
        this.indexS = indexS;

        result = new ArrayList<Datum[]>();

        HashMap<Integer, ArrayList<Datum[]>> build = new HashMap<Integer, ArrayList<Datum[]>>(401);

        Datum[] outTpl;

        //build phase
        Datum[] tuple;
        while ((tuple = R.readOneTuple()) != null) {
            int hashKey = hashFunction(tuple[indexR], 401);
            ArrayList<Datum[]> list = build.get(hashKey);
            if (list == null) {
                list = new ArrayList<Datum[]>();
            }
            list.add(tuple);
            build.put(hashKey, list);
        }

        //probe phase
        while ((tuple = S.readOneTuple()) != null) {
            int hashKey = hashFunction(tuple[indexS], 401);
            ArrayList<Datum[]> list = build.get(hashKey);
            if (list == null)
                continue;
            for (Datum[] tplR : list) {
                if (tplR[indexR].equals(tuple[indexS])) {
                    int counter = 0;
                    outTpl = new Datum[R.getSchema().length + S.getSchema().length];
                    for (Datum val : tplR) {
                        outTpl[counter] = val;
                        counter++;
                    }
                    for (Datum val : tuple) {
                        outTpl[counter] = val;
                        counter++;
                    }
                    result.add(outTpl);
                }
            }
        }

        updateSchema();
        reset();

    }

    public void updateSchema() {
        outputSchema = new ColumnSchema[R.getSchema().length + S.getSchema().length];
        int i = 0;
        for (ColumnSchema schema : R.getSchema()) {
            outputSchema[i] = schema;
            i++;
        }
        for (ColumnSchema schema : S.getSchema()) {
            outputSchema[i] = schema;
            i++;
        }
    }

    public int hashFunction(Datum value, int size) {
        try {
            if (value.getType() == Datum.type.LONG) {
                return value.toLONG().intValue() % size;
            } else if (value.getType() == Datum.type.DOUBLE) {
                return value.toDOUBLE().intValue() % size;
            } else if (value.getType() == Datum.type.STRING ||
                    value.getType() == Datum.type.DATE) {
                return value.toSTRING().toString().length() % size;
            }
        } catch (CastException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public Datum[] readOneTuple() {
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    @Override
    public void reset() {
        iter = result.iterator();
    }

    @Override
    public ColumnSchema[] getSchema() {
        return outputSchema;
    }

    @Override
    public Long getProbableTableSize() {
        return R.getProbableTableSize() * S.getProbableTableSize();
    }

}