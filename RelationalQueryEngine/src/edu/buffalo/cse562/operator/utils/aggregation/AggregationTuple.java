package edu.buffalo.cse562.operator.utils.aggregation;

import edu.buffalo.cse562.data.Datum;

import java.util.*;

import static edu.buffalo.cse562.SchemaIndexConstants.getOldIndexReferencedByFunction;
import static edu.buffalo.cse562.SchemaIndexConstants.isFunctionWithoutExpression;

public class AggregationTuple implements Comparable<AggregationTuple> {

    //consolidated tuple to return
    private Datum[] consolidatedTuple;

    //set of distinct values for each column in distinct claused column
    private Map<Integer, Set<Datum>> distinctElements;
    private List<Integer> indexesOfDistinctClausedColumnsInOldSchema;

    private Comparator<Datum[]> groupByComparator;

    /**
     * dummy constructor to compare this object with Datum[] with less setup
     */
    public AggregationTuple(Datum[] consolidatedTuple, Comparator<Datum[]> groupByComparator) {
        this.consolidatedTuple = consolidatedTuple;
        this.groupByComparator = groupByComparator;
    }

    public AggregationTuple(Datum[] consolidatedTuple, Comparator<Datum[]> groupByComparator, List<Integer> indexesOfDistinctClausedColumnsInOldSchema) {
        this.groupByComparator = groupByComparator;
        this.consolidatedTuple = consolidatedTuple;
        this.indexesOfDistinctClausedColumnsInOldSchema = new ArrayList<>();
        distinctElements = new HashMap<>();

        int index;
        for (int i = 0; i < indexesOfDistinctClausedColumnsInOldSchema.size(); i++) {
            if (isFunctionWithoutExpression(indexesOfDistinctClausedColumnsInOldSchema.get(i))) {
                index = getOldIndexReferencedByFunction(indexesOfDistinctClausedColumnsInOldSchema.get(i));
            } else {
                index = i;
            }

            distinctElements.put(index, new HashSet<Datum>());
            this.indexesOfDistinctClausedColumnsInOldSchema.add(index);
        }
    }

    public Datum[] getUnderlyingConsolidatedTuple() {
        return consolidatedTuple;
    }

    public void updateDistinctElementsSet(Datum[] newTuple) {
        for (Integer distinctIndex : indexesOfDistinctClausedColumnsInOldSchema) {
            distinctElements.get(distinctIndex).add(newTuple[distinctIndex]);
        }
    }

    public void addToDistinctElementsSet(Integer oldIndex, Datum newCell) {
        if (indexesOfDistinctClausedColumnsInOldSchema.contains(oldIndex)) {
            distinctElements.get(oldIndex).add(newCell);
        }
    }

    public int getNoOfDistinctValuesOfColumnWithIndex(int index) {
        return distinctElements.get(index).size();
    }

    @Override
    public int compareTo(AggregationTuple that) {
        return groupByComparator.compare(consolidatedTuple, that.consolidatedTuple);
    }

    @Override
    public boolean equals(Object obj) {
        return groupByComparator.compare(consolidatedTuple, ((AggregationTuple) obj).consolidatedTuple) == 0;
    }
}
