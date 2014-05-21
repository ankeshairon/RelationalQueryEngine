package edu.buffalo.cse562.visitor;

import edu.buffalo.cse562.data.DOUBLE;
import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.data.LONG;
import edu.buffalo.cse562.data.STRING;
import jdbm.SecondaryTreeMap;
import jdbm.btree.BTreeSecondarySortedMap;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;

public class IndexDataMapVisitor extends AbstractExpressionVisitor {

    private BTreeSecondarySortedMap<Datum, Long, String> secondaryMap;
    private String colName;

    private List<Long> rowIds;
    private Datum key1;
    private Datum key2;

    public IndexDataMapVisitor(SecondaryTreeMap<Datum, Long, String> secondaryMap, String colName) {
        this.secondaryMap = (BTreeSecondarySortedMap<Datum, Long, String>) secondaryMap;
        this.colName = colName;
    }

    @Override
    public void visit(Function arg0) {
        if (arg0.getName().equalsIgnoreCase("date")) {
            ((Expression) arg0.getParameters().getExpressions().get(0)).accept(this);
        } else {
            throw new UnsupportedOperationException(arg0.getName() + " function not supported");
        }
    }

    @Override
    public void visit(DoubleValue arg0) {
        saveKeyValue(new DOUBLE(arg0.getValue()));
    }

    @Override
    public void visit(LongValue arg0) {
        saveKeyValue(new LONG(arg0.getValue()));
    }

    @Override
    public void visit(DateValue arg0) {
        saveKeyValue(new STRING(arg0.toString()));
    }

    @Override
    public void visit(StringValue arg0) {
        saveKeyValue(new STRING(arg0.getValue()));
    }

    @Override
    public void visit(AndExpression arg0) {
        arg0.getLeftExpression().accept(this);
        List<Long> rowIds1 = rowIds;
        rowIds = null;

        arg0.getRightExpression().accept(this);
        rowIds.retainAll(rowIds1);
    }

    @Override
    public void visit(EqualsTo arg0) {
        populateColumnAndKey(arg0);

        rowIds = (ArrayList<Long>) secondaryMap.get(key1);
    }

    @Override
    public void visit(GreaterThan arg0) {
        populateColumnAndKey(arg0);
        addRowIdsGreaterThanKey(false);
    }

    @Override
    public void visit(GreaterThanEquals arg0) {
        populateColumnAndKey(arg0);
        addRowIdsGreaterThanKey(true);
    }

    @Override
    public void visit(MinorThan arg0) {
        populateColumnAndKey(arg0);
        addRowIdsLesserThanKey(false);
    }

    @Override
    public void visit(MinorThanEquals arg0) {
        populateColumnAndKey(arg0);
        addRowIdsLesserThanKey(true);
    }

    @Override
    public void visit(NotEqualsTo arg0) {
        populateColumnAndKey(arg0);

        Iterator<Datum> iterator = secondaryMap.keySet().iterator();
        rowIds = new ArrayList<>();
        Datum iteratorKey;

        while (iterator.hasNext()) {
            iteratorKey = iterator.next();
            if (!iteratorKey.equals(key1)) {
                rowIds.addAll((List<Long>) secondaryMap.get(iteratorKey));
            }
        }
    }

    @Override
    public void visit(Column arg0) {
//        try {
        assert (arg0.getColumnName().equalsIgnoreCase(colName));
//        } catch (AssertionError e) {
//            e.printStackTrace();
//        }
    }

    private void saveKeyValue(Datum key) {
//        if (key1 == null) {
//            key1 = key;
//        } else {
//            if (key1.compareTo(key) <= 0) {
//                key2 = key;
//            } else {
//                key2 = key1;
        key1 = key;
//            }
//        }
    }

    private void addRowIdsGreaterThanKey(boolean isInclusive) {
        final SortedMap<Datum, Iterable<Long>> submap = secondaryMap.tailMap(key1); //keys are greater than OR EQUAL
        final Iterator<Datum> iterator = submap.keySet().iterator();

        if (!isInclusive) {
            if (iterator.hasNext()) {
                iterator.next();    //skip the first element
            }
        }
        addAllElementsFromSubmap(submap, iterator);
    }

    private void addRowIdsLesserThanKey(boolean isInclusive) {
        final SortedMap<Datum, Iterable<Long>> submap = secondaryMap.headMap(key1); //keys are STRICTLY LESS THAN
        final Iterator<Datum> iterator = submap.keySet().iterator();

        addAllElementsFromSubmap(submap, iterator);

        if (isInclusive) {
            final Iterable<Long> rowIds = secondaryMap.get(key1);
            if (rowIds != null) {
                this.rowIds.addAll((List<Long>) rowIds);
            }
        }
    }

    private void addAllElementsFromSubmap(SortedMap<Datum, Iterable<Long>> submap, Iterator<Datum> keySetIterator) {
        rowIds = new ArrayList<>();
        while (keySetIterator.hasNext()) {
            rowIds.addAll((List<Long>) submap.get(keySetIterator.next()));
        }
    }

    public void populateColumnAndKey(BinaryExpression binaryExpression) {
        binaryExpression.getLeftExpression().accept(this);
        binaryExpression.getRightExpression().accept(this);
    }

    public List<Long> getRowIds() {
        return rowIds;
    }

}
