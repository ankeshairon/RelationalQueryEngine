package edu.buffalo.cse562.model.aggregation;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.expression.Expression;

import java.util.ArrayList;
import java.util.List;

public class AverageAggregation extends Aggregation {
    private List<Float> sums;
    private List<Float> counts;

    public AverageAggregation(ColumnSchema[] oldSchema, Expression expression, List<Integer> indexesOfGroupByReferencesInOldSchema) {
        super(oldSchema, expression, indexesOfGroupByReferencesInOldSchema);
        instantiateLists();

    }

    protected void instantiateLists() {
        super.instantiateLists();
        sums = new ArrayList<>();
        counts = new ArrayList<>();
        for (int i = 0; i < indexesOfGroupByReferencesInOldSchema.size(); i++) {
            sums.add(0f);
            counts.add(0f);
        }
    }


    @Override
    public void process(Datum[] tuple) {
        Float fieldValue = getFieldValue(tuple);
        if (!"".equals(fieldValue)) {
            try {
//                sums = sums + fieldValue;
//                counts++;
            } catch (NumberFormatException e) {
                System.out.println("Warning!! Non-numeric data not supported for addition. Skipping current tuple");
            }
        }

    }

    @Override
    public String getValue() {
//        return String.valueOf(sums / counts);
        return null;
    }

}
