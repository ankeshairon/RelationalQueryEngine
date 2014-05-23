package edu.buffalo.cse562.operator.abstractoperators;

import edu.buffalo.cse562.schema.ColumnSchema;

public abstract class JoinOperator implements Operator {
    protected ColumnSchema[] outputSchema;
    protected Operator R;
    protected Operator S;

    protected JoinOperator(Operator r, Operator s) {
        R = r;
        S = s;
        updateSchema();
    }

    public Operator getR() {
        return R;
    }

    public Operator getS() {
        return S;
    }

    @Override
    public Long getProbableTableSize() {
        return R.getProbableTableSize() * S.getProbableTableSize();
    }

    @Override
    public ColumnSchema[] getSchema() {
        return outputSchema;
    }

    private void updateSchema() {
        outputSchema = new ColumnSchema[R.getSchema().length + S.getSchema().length];
        System.arraycopy(R.getSchema(), 0, outputSchema, 0, R.getSchema().length);
        System.arraycopy(S.getSchema(), 0, outputSchema, R.getSchema().length, S.getSchema().length);
    }
}
