package edu.buffalo.cse562.model;

public class Pair<T1, T2> {
    private T1 first;
    private T2 second;

    public Pair() {
    }

    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) {
            return false;
        }

        Pair that = (Pair) o;
        Object thatFirst = that.getFirst();
        if (first == thatFirst || (first != null && first.equals(thatFirst))) {
            Object thatSecond = that.getSecond();
            if (second == thatSecond || (second != null && second.equals(thatSecond))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "<" + first.toString() + "," + second.toString() + ">";
    }

    public T1 getFirst() {
        return first;
    }

    public void setFirst(T1 first) {
        this.first = first;
    }

    public T2 getSecond() {
        return second;
    }

    public void setSecond(T2 second) {
        this.second = second;
    }
}
