package edu.buffalo.cse562.model.data;

import java.util.ArrayList;

public class Tuple {
	public ArrayList<String> fields;

    public Tuple() {
        fields = new ArrayList<>();
    }

    @Override
    public String toString() {
        return fields.toString();
    }
}
