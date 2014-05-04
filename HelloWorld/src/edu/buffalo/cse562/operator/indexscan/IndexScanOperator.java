package edu.buffalo.cse562.operator.indexscan;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.operator.Operator;
import edu.buffalo.cse562.operator.ScanOperator;
import edu.buffalo.cse562.schema.ColumnSchema;
import jdbm.PrimaryStoreMap;
import net.sf.jsqlparser.expression.Expression;

import java.util.List;
import java.util.ListIterator;

import static edu.buffalo.cse562.operator.utils.ScanUtils.getDatumsForRelevantColumnPositions;

public class IndexScanOperator implements Operator {

    private final PrimaryStoreMap<Long, String> storeMap;
    private ListIterator<Long> keyListIterator;
    private final List<Long> keyList;
    private final ScanOperator in;

    public IndexScanOperator(Operator in, List<Expression> conditions) {
        this.in = (ScanOperator) in;

        final IndexScanHelper helper = new IndexScanHelper(in.getSchema(), conditions);
        keyList = helper.getFilteredRowIds();
        storeMap = helper.getStoreMap();
        reset();
    }

    @Override
    public Datum[] readOneTuple() {
        if (!keyListIterator.hasNext()) {
            return null;
        }

        return getDatumsForRelevantColumnPositions(
                storeMap.get(keyListIterator.next()),
                in.getRelevantColumnIndexes(),
                in.getSchema());
    }

    @Override
    public void reset() {
        keyListIterator = keyList.listIterator();
    }

    @Override
    public ColumnSchema[] getSchema() {
        return in.getSchema();
    }

    @Override
    public Long getProbableTableSize() {
        return new Long(keyList.size() * 100);
    }
}
