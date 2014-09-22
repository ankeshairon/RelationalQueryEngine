package edu.buffalo.cse562.model;

import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

public class ColumnWrapper extends Column {

    private String fullyQualifiedName;
    private Column parent;

    public ColumnWrapper(Column parent) {
        this.parent = parent;
        fullyQualifiedName = parent.getTable().getName() + "." + parent.getColumnName();
    }


    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ColumnWrapper) && fullyQualifiedName.equals(((ColumnWrapper) obj).fullyQualifiedName);
    }

    @Override
    public String toString() {
        return fullyQualifiedName;
    }

    @Override
    public int hashCode() {
        return fullyQualifiedName.hashCode();
    }

    @Override
    public String getColumnName() {
        return parent.getColumnName();
    }

    @Override
    public Table getTable() {
        return parent.getTable();
    }

    @Override
    public void setColumnName(String string) {
        parent.setColumnName(string);
    }

    @Override
    public void setTable(Table table) {
        parent.setTable(table);
    }

    @Override
    public String getWholeColumnName() {
        return parent.getWholeColumnName();
    }

    @Override
    public void accept(ExpressionVisitor expressionVisitor) {
        parent.accept(expressionVisitor);
    }
}
