package edu.buffalo.cse562.model.aggregation;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.expression.Expression;

import java.util.ArrayList;
import java.util.List;

public class CountAggregation extends Aggregation {
    private List<Float> counts;

    public CountAggregation(ColumnSchema[] oldSchema, Expression expression, List<Integer> indexesOfGroupByReferencesInOldSchema) {
        super(oldSchema, expression, indexesOfGroupByReferencesInOldSchema);
        instantiateLists();
    }

    protected void instantiateLists() {
        super.instantiateLists();
        counts = new ArrayList<>();
        for (int i = 0; i < indexesOfGroupByReferencesInOldSchema.size(); i++) {
            counts.add(0f);
        }

    }

    @Override
    public void process(Datum[] tuple) {
        for (Integer indexOfInterest : indexesOfGroupByReferencesInOldSchema) {
//            try {
//                tuple[indexOfInterest].toFLOAT() ==
//            } catch (Datum.CastException e) {
//
//
//            }
        }


        if (!"".equals(getFieldValue(tuple))) {
//            counts++;
        }
    }

    @Override
    public String getValue() {
        return String.valueOf(counts);
    }
}
