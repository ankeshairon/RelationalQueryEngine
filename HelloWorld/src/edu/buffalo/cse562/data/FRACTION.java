package edu.buffalo.cse562.data;

public class FRACTION extends DOUBLE {
    private static final long serialVersionUID = -48801923424168509L;

    Double numerator;
    Double denominator;

    /**
     * only to be used for externalization. Do not use this
     */
    public FRACTION() {
    }

    public FRACTION(Double numerator, Double denominator) {
        super(numerator / denominator);
        this.numerator = numerator;
        this.denominator = denominator;
    }

    @Override
    public Double toDOUBLE() throws CastException {
        return numerator / denominator;
    }

    public Double getNumerator() {
        return numerator;
    }

    public void setNumerator(Double numerator) {
        this.numerator = numerator;
    }

    public Double getDenominator() {
        return denominator;
    }

    public void setDenominator(Double denominator) {
        this.denominator = denominator;
    }

    @Override
    public int compareTo(Object o) {
        FRACTION that = (FRACTION) o;
        return ((Double) (numerator / denominator)).compareTo(that.numerator / that.denominator);
    }
}
