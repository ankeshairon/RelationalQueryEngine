/*
 * @author: Dev Bharadwaj
 * From type can be of three types.
 * So the source can be of three types we will read the data in here.
 */
package edu.buffalo.cse562.parser.datavisitors;

import edu.buffalo.cse562.model.operators.FromOperator;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;

public class FromItemVisitorImpl implements FromItemVisitor {

    private FromOperator fromOperator;

    public FromItemVisitorImpl(FromOperator fromOperator) {
        this.fromOperator = fromOperator;
    }

    @Override
    public void visit(Table table) {
        fromOperator.setSourceTableName(table.getName());
    }

    @Override
    public void visit(SubSelect arg0) {
        // create a new TreeMaker for subselect and get output and assign to ResultSet

    }

    @Override
    public void visit(SubJoin arg0) {
        // A table created by "(tab1 join tab2)", assign to ResultSet


    }

}
