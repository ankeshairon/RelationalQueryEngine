/*
 * @author: Dev Bharadwaj
 * From type can be of three types.
 * So the source can be of three types we will read the data in here.
 */
package edu.buffalo.cse562.parser.datavisitors;

import edu.buffalo.cse562.datagrabber.DataGrabber;
import edu.buffalo.cse562.model.operators.sourceoperators.FromOperator;
import edu.buffalo.cse562.processor.DataProcessor;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;

public class FromItemVisitorImpl implements FromItemVisitor {

    private FromOperator fromOperator;
    private DataGrabber dataGrabber;

    public FromItemVisitorImpl(FromOperator fromOperator, DataGrabber dataGrabber) {
        this.fromOperator = fromOperator;
        this.dataGrabber = dataGrabber;
    }

    @Override
    public void visit(Table table) {
        fromOperator.setSourceTableName(table.getName());
    }

    @Override
    public void visit(SubSelect subSelect) {
        //todo Use SelectionOperator to retrieve results from this
        DataProcessor treeMaker = new DataProcessor(dataGrabber);
        SelectDataVisitorImpl selectVisitor = new SelectDataVisitorImpl(treeMaker);
        subSelect.getSelectBody().accept(selectVisitor);
        treeMaker.execute(null);
    }

    @Override
    public void visit(SubJoin arg0) {
        // A table created by "(tab1 join tab2)", assign to ResultSet


    }

}
