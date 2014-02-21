/*Author - Ankesh Airon
 *ankeshai@buffalo.edu
 *Person no - 50096547
 *
 *Co-author: Dev Bharadwaj
*/
package edu.buffalo.cse562.model.operators;

import edu.buffalo.cse562.model.data.ResultSet;
import edu.buffalo.cse562.model.operatorabstract.UnaryOperator;
import net.sf.jsqlparser.expression.Expression;

import java.util.LinkedHashMap;

public class ProjectionOperator implements UnaryOperator {

    private Integer currentIndex;
    private LinkedHashMap<Integer, String> columnNames;
    private LinkedHashMap<Integer, AggregateOperator> aggregates;
    private ResultSet resultSet;

    public ProjectionOperator() {
        currentIndex = 0;
        columnNames = new LinkedHashMap<>();
        aggregates = new LinkedHashMap<>();
    }

    @Override
    public void dataIn(ResultSet inputDataSet) {
        //todo implement projection operator
        //make calls to resultSet manipulating class & populate resultset inside it


    }

    @Override
    public ResultSet dataOut() {
        return resultSet;
    }

    public void addProjectionAttribute(String columnName) {
        columnNames.put(currentIndex, columnName);
        ++currentIndex;
    }

    public void addProjectionAttribute(Expression aggregationExpression) {
        aggregates.put(currentIndex, new AggregateOperator(aggregationExpression));
        ++currentIndex;
    }

}
