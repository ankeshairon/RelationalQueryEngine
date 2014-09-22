package edu.buffalo.cse562.visitor;

import net.sf.jsqlparser.expression.*;


public class StringValueExtractor extends AbstractExpressionVisitor {
    public String value;

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
        value = arg0.toString();
    }

    @Override
    public void visit(LongValue arg0) {
        value = arg0.toString();
    }

    @Override
    public void visit(DateValue arg0) {
        value = arg0.toString();
    }

    @Override
    public void visit(StringValue arg0) {
        value = arg0.getValue();
    }

    public String getValue() {
        return value;
    }
}
