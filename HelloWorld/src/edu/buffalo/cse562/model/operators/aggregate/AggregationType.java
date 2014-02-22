package edu.buffalo.cse562.model.operators.aggregate;

public enum AggregationType {
    AVERAGE,
    SUM,
    COUNT;

    public static AggregationType getAggregationType(String aggregationName) {
        switch (aggregationName) {
            case "sum":
            case "SUM":
                return SUM;
            case "count":
            case "COUNT":
                return COUNT;
            case "avg":
            case "AVG":
                return AVERAGE;

        }
        throw new UnsupportedOperationException("Unsupported aggregation received " + aggregationName);
    }
}
