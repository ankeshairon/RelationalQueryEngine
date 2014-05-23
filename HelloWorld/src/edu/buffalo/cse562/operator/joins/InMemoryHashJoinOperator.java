package edu.buffalo.cse562.operator.joins;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.operator.abstractoperators.JoinOperator;
import edu.buffalo.cse562.operator.abstractoperators.Operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class InMemoryHashJoinOperator extends JoinOperator {

    int indexR, indexS;

    ArrayList<Datum[]> result;
    Iterator<Datum[]> iter;

    public InMemoryHashJoinOperator(Operator R, Operator S, int indexR, int indexS) {
        super(R, S);
        this.indexR = indexR;
        this.indexS = indexS;

        result = new ArrayList<>();

        HashMap<Integer, ArrayList<Datum[]>> build = new HashMap<>(401);

        ArrayList<Datum[]> list;
        Datum[] outTpl;
        int hashKey;

        //build phase
        Datum[] tuple;
        while ((tuple = R.readOneTuple()) != null) {
            hashKey = tuple[indexR].customHash(401);
            list = build.get(hashKey);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(tuple);
            build.put(hashKey, list);
        }

        //probe phase
        while ((tuple = S.readOneTuple()) != null) {
            hashKey = tuple[indexS].customHash(401);
            list = build.get(hashKey);
            if (list == null)
                continue;
            for (Datum[] tplR : list) {
                if (tplR[indexR].equals(tuple[indexS])) {
                    outTpl = new Datum[outputSchema.length];
                    System.arraycopy(tplR, 0, outTpl, 0, tplR.length);
                    System.arraycopy(tuple, 0, outTpl, tplR.length, tuple.length);
                    result.add(outTpl);
                }
            }
        }
        reset();
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
}