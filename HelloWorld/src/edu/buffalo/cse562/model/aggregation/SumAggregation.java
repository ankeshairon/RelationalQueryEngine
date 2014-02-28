package edu.buffalo.cse562.model.aggregation;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.expression.Expression;

import java.util.ArrayList;
import java.util.List;

public class SumAggregation extends Aggregation {

    private List<Float> sums;

    public SumAggregation(ColumnSchema[] oldSchema, Expression expression, List<Integer> indexesOfGroupByReferencesInOldSchema) {
        super(oldSchema, expression, indexesOfGroupByReferencesInOldSchema);
        instantiateLists();
    }

    protected void instantiateLists() {
        super.instantiateLists();
        sums = new ArrayList<>();
        for (int i = 0; i < indexesOfGroupByReferencesInOldSchema.size(); i++) {
            sums.add(0f);
        }
    }

    @Override
    public void process(Datum[] tuple) {
        Float fieldValue = getFieldValue(tuple);
        if (!"".equals(fieldValue)) {
            try {
//                sums = sums + fieldValue;
            } catch (NumberFormatException e) {
                System.out.println("Warning!! Non-numeric data not supported for addition. Skipping current tuple");
            }
        }
    }

    @Override
    public String getValue() {
        return String.valueOf(sums);
    }
}
