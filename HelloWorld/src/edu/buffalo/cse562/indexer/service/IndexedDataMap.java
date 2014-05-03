package edu.buffalo.cse562.indexer.service;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.visitor.AbstractExpressionVisitor;
import edu.buffalo.cse562.visitor.IndexDataMapVisitor;
import jdbm.SecondaryTreeMap;
import net.sf.jsqlparser.expression.Expression;

import java.util.ArrayList;
import java.util.List;

public class IndexedDataMap extends AbstractExpressionVisitor {

    private final SecondaryTreeMap<Datum, Long, String> secondaryMap;
    private final String colName;

    public IndexedDataMap(SecondaryTreeMap<Datum, Long, String> secondaryMap, String colName) {
        this.secondaryMap = secondaryMap;
        this.colName = colName;
    }

    /**
     * param logicalOperator is the logical comparison (=, <, >, <>, <=, etc)
     * param value is the value to which it is being compared
     *
     * e.g. To return tuple where C < 3, use getRowIdsWhereColumnIsXThanValue("<", new Datum(3))
     */
    public List<Long> getRowIdsWhereColumnIsXThanValue(Expression condition) {
        IndexDataMapVisitor visitor = new IndexDataMapVisitor(secondaryMap, colName);
        condition.accept(visitor);
        return visitor.getRowIds();
    }

    public List<Long> getRowIdsForKey(Datum key) {
        return (List<Long>) secondaryMap.get(key);
    }

    public List<String> getTuplesForIds(List<Long> ids) {
        List<String> tuples = new ArrayList<>();
        for (Long id : ids) {
            tuples.add(getTupleForId(id));
        }
        return tuples;
    }

    public String getTupleForId(Long id) {
        return secondaryMap.getPrimaryValue(id);
    }

}
