package edu.buffalo.cse562.operator;

public abstract class JoinOperator implements Operator {
    protected Operator R;
    protected Operator S;

    protected JoinOperator(Operator r, Operator s) {
        R = r;
        S = s;
    }

    public Operator getR() {
        return R;
    }

    public Operator getS() {
        return S;
    }
}
