package edu.buffalo.cse562.indexer.service;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.visitor.AbstractExpressionVisitor;
import edu.buffalo.cse562.visitor.IndexDataMapVisitor;
import jdbm.SecondaryTreeMap;
import net.sf.jsqlparser.expression.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * This class interacts with SecondaryStoreMap & has methods requiring column names (SecondaryIndexes)
 */
public class IndexedDataMap extends AbstractExpressionVisitor {

    private final SecondaryTreeMap<Datum, Long, String> secondaryMap;
    private final String colName;

    public IndexedDataMap(SecondaryTreeMap<Datum, Long, String> secondaryMap, String colName) {
        this.secondaryMap = secondaryMap;
        this.colName = colName;
    }

    public List<Long> getRowIdsForCondition(Expression condition) {
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
