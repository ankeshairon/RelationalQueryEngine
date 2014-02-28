package edu.buffalo.cse562.model.aggregation;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.schema.ColumnSchema;
import net.sf.jsqlparser.expression.Expression;

import java.util.ArrayList;
import java.util.List;

public abstract class Aggregation {

    private final ColumnSchema[] oldSchema;
    private Expression expressionToBeEvaluated;
    protected List<Integer> indexesOfGroupByReferencesInOldSchema;
    protected List<Datum[]> lastAccessedRow;

    public Aggregation(ColumnSchema[] oldSchema, Expression expressionToBeEvaluated, List<Integer> indexesOfGroupByReferencesInOldSchema) {
        this.oldSchema = oldSchema;
        this.expressionToBeEvaluated = expressionToBeEvaluated;
        this.indexesOfGroupByReferencesInOldSchema = indexesOfGroupByReferencesInOldSchema;
    }

    public abstract String getValue();

    public abstract void process(Datum[] tuple);

    protected Float getFieldValue(Datum[] tuple) {
        //todo call Dev's code to evaluate expressions life "valueof(Field1) + valueof(Field2)"
//      return  functionToCall(tuple, expressionToBeEvaluated)
        return 1f;
    }

    protected void instantiateLists() {
        lastAccessedRow = new ArrayList<>();
        for (int i = 0; i < indexesOfGroupByReferencesInOldSchema.size(); i++) {
            lastAccessedRow.add(null);
        }
    }
}

