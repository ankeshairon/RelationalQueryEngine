/*
 * @author: Dev Bharadwaj
 * From type can be of three types.
 * So the source can be of three types we will read the data in here.
 */
package edu.buffalo.cse562.parser.datavisitors;

import edu.buffalo.cse562.model.data.ResultSet;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;

public class FromItemVisitorImpl implements FromItemVisitor {

    ResultSet resultSet;


    public ResultSet getResultSet() {
        return resultSet;
    }

    @Override
    public void visit(Table arg0) {
        // read from table.dat file and assign to ResultSet
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
