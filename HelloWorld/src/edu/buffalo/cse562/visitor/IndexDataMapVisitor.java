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

import java.util.*;

public class IndexDataMapVisitor extends AbstractExpressionVisitor {

    private BTreeSecondarySortedMap<Datum, Long, String> secondaryMap;
    private String colName;

    private List<Long> rowIds;
    private Datum key1;
    private Datum key2;

    private Boolean isRangeLookup;
    private Boolean isInclusive1;
    private Boolean isInclusive2;
    private Boolean isInequality;

    private int currentVariable;

    public IndexDataMapVisitor(SecondaryTreeMap<Datum, Long, String> secondaryMap, String colName) {
        this.secondaryMap = (BTreeSecondarySortedMap<Datum, Long, String>) secondaryMap;
        this.colName = colName;
        isRangeLookup = false;
        currentVariable = 0;
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
        isRangeLookup = true;

        currentVariable = 1;
        arg0.getLeftExpression().accept(this);

        currentVariable = 2;
        arg0.getRightExpression().accept(this);

        if (isInequality != null && isInequality) {
            isRangeLookup = false;
            arg0.getLeftExpression().accept(this);

        } else {
            if (key2.compareTo(key1) < 0) {
                Boolean tempIsInclusive;
                Datum tempKey;

                tempKey = key2;
                key2 = key1;
                key1 = tempKey;

                tempIsInclusive = isInclusive2;
                isInclusive2 = isInclusive1;
                isInclusive1 = tempIsInclusive;
            }

            final SortedMap<Datum, Iterable<Long>> submap = secondaryMap.subMap(key1, key2); //key1 inclusive key2 exclusive
            final Iterator<Datum> iterator = submap.keySet().iterator();

            skipFirstElementIfNotInclusive(isInclusive1, iterator);
            addAllElementsFromSubmap(submap, iterator);
            addLastElementIfIsInclusive(isInclusive2, key2);
        }
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
        if (isRangeLookup) {
            isInequality = true;
            return;
        }

        final Set<Datum> keySet = new HashSet<>(secondaryMap.keySet());
        keySet.remove(key1);
        if (key2 != null) {
            keySet.remove(key2);
        }

        Iterator<Datum> iterator = keySet.iterator();
        rowIds = new ArrayList<>();
        while (iterator.hasNext()) {
            rowIds.addAll((List<Long>) secondaryMap.get(iterator.next()));
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
        if (isRangeLookup) {
            if (currentVariable == 1) {
                key1 = key;
            } else {
                key2 = key;
            }
        } else {
            key1 = key;
        }
    }

    private void addRowIdsGreaterThanKey(boolean isInclusive) {
        if (isRangeLookup) {
            setIsInclusive(isInclusive);
            return;
        }

        final SortedMap<Datum, Iterable<Long>> submap = secondaryMap.tailMap(key1); //keys are greater than OR EQUAL
        final Iterator<Datum> iterator = submap.keySet().iterator();

        skipFirstElementIfNotInclusive(isInclusive, iterator);
        addAllElementsFromSubmap(submap, iterator);
    }

    private void skipFirstElementIfNotInclusive(boolean isInclusive, Iterator<Datum> iterator) {
        if (!isInclusive) {
            if (iterator.hasNext()) {
                iterator.next();    //skip the first element
            }
        }
    }

    private void addRowIdsLesserThanKey(boolean isInclusive) {
        if (isRangeLookup) {
            setIsInclusive(isInclusive);
            return;
        }

        final SortedMap<Datum, Iterable<Long>> submap = secondaryMap.headMap(key1); //keys are STRICTLY LESS THAN
        final Iterator<Datum> iterator = submap.keySet().iterator();

        addAllElementsFromSubmap(submap, iterator);
        addLastElementIfIsInclusive(isInclusive, key1);
    }

    private void addLastElementIfIsInclusive(boolean isInclusive, Datum key) {
        if (isInclusive) {
            final Iterable<Long> rowIds = secondaryMap.get(key);
            if (rowIds != null) {
                this.rowIds.addAll((List<Long>) rowIds);
            }
        }
    }

    private void setIsInclusive(boolean isInclusive) {
        if (currentVariable == 1) {
            isInclusive1 = isInclusive;
        } else {
            isInclusive2 = isInclusive;
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
