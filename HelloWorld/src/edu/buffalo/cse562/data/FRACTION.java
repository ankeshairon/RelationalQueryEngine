package edu.buffalo.cse562.data;

public class FRACTION extends FLOAT {
    float numerator;
    float denominator;

    public FRACTION(float numerator, float denominator) {
        super(numerator / denominator);
        this.numerator = numerator;
        this.denominator = denominator;
    }

    @Override
    public Float toFLOAT() throws CastException {
        return numerator / denominator;
    }

    public float getNumerator() {
        return numerator;
    }

    public void setNumerator(float numerator) {
        this.numerator = numerator;
    }

    public float getDenominator() {
        return denominator;
    }

    public void setDenominator(float denominator) {
        this.denominator = denominator;
    }
}
