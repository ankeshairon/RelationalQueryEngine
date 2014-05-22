package edu.buffalo.cse562.visitor;

import edu.buffalo.cse562.model.ColumnWrapper;
import edu.buffalo.cse562.model.TableInfo;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;

import java.util.*;

import static edu.buffalo.cse562.schema.SchemaUtils.getColumnIndexInColDefn;

public class ScanOptimizationVisitor implements OrderByVisitor, SelectItemVisitor, ExpressionVisitor, FromItemVisitor, SelectVisitor {

    private HashMap<String, TableInfo> tablesInfo;

    private Map<String, Set<Column>> columnsToProject;
    private Map<String, String> aliasNameMap;

    private String singletonTableName;

    public ScanOptimizationVisitor(Select statement, HashMap<String, TableInfo> tablesInfo) {
        this.tablesInfo = tablesInfo;
        columnsToProject = new HashMap<>();
        aliasNameMap = new HashMap<>();
        statement.getSelectBody().accept(this);
    }

    @Override
    public void visit(NullValue nullValue) {
        throw new UnsupportedOperationException(nullValue.getClass().getName());
    }

    @Override
    public void visit(Function function) {
        final ExpressionList parameters = function.getParameters();
        if (parameters != null) {
            ((Expression) parameters.getExpressions().get(0)).accept(this);
        }
    }

    @Override
    public void visit(InverseExpression inverseExpression) {
        throw new UnsupportedOperationException(inverseExpression.getClass().getName());
    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {
        throw new UnsupportedOperationException(jdbcParameter.getClass().getName());
    }

    @Override
    public void visit(DoubleValue doubleValue) {
        //to be left empty
    }

    @Override
    public void visit(LongValue longValue) {
        //to be left empty
    }

    @Override
    public void visit(DateValue dateValue) {
        //to be left empty
    }

    @Override
    public void visit(TimeValue timeValue) {
        //to be left empty
    }

    @Override
    public void visit(TimestampValue timestampValue) {
        //to be left empty
    }

    @Override
    public void visit(Parenthesis parenthesis) {
        parenthesis.getExpression().accept(this);
    }

    @Override
    public void visit(StringValue stringValue) {
        //to be left empty
    }

    @Override
    public void visit(Addition addition) {
        visitBinaryExpression(addition);
    }

    @Override
    public void visit(Division division) {
        visitBinaryExpression(division);
    }

    @Override
    public void visit(Multiplication multiplication) {
        visitBinaryExpression(multiplication);
    }

    @Override
    public void visit(Subtraction subtraction) {
        visitBinaryExpression(subtraction);
    }

    @Override
    public void visit(AndExpression andExpression) {
        visitBinaryExpression(andExpression);
    }

    @Override
    public void visit(OrExpression orExpression) {
        visitBinaryExpression(orExpression);
    }

    @Override
    public void visit(Between between) {
        throw new UnsupportedOperationException(between.getClass().getName());
    }

    @Override
    public void visit(EqualsTo equalsTo) {
        visitBinaryExpression(equalsTo);
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        visitBinaryExpression(greaterThan);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        visitBinaryExpression(greaterThanEquals);
    }

    @Override
    public void visit(InExpression inExpression) {
        throw new UnsupportedOperationException(inExpression.getClass().getName());
    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        throw new UnsupportedOperationException(isNullExpression.getClass().getName());
    }

    @Override
    public void visit(LikeExpression likeExpression) {
        visitBinaryExpression(likeExpression);
    }

    @Override
    public void visit(MinorThan minorThan) {
        visitBinaryExpression(minorThan);
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        visitBinaryExpression(minorThanEquals);
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        visitBinaryExpression(notEqualsTo);
    }

    @Override
    public void visit(Column tableColumn) {
        String tableName = tableColumn.getTable().getName();

        if (tableName == null) {
            if (singletonTableName != null) {
                tableName = singletonTableName;
            } else {
                tableName = searchForColumnInKnownTables(tableColumn.getColumnName());
            }
        }
        if (tableName != null) {
            Set<Column> columnSet = columnsToProject.get(tableName);
            if (columnSet == null) {
                columnSet = new HashSet<>();
                columnsToProject.put(tableName, columnSet);
            }
            columnSet.add(new ColumnWrapper(tableColumn));
        }
    }

    @Override
    public void visit(Table table) {
        final String name = table.getName();
        singletonTableName = name;

        final String alias = table.getAlias();
        if (alias != null) {
            aliasNameMap.put(alias, name);
        }
    }

    @Override
    public void visit(SubSelect subSelect) {
        subSelect.getSelectBody().accept(this);
    }

    @Override
    public void visit(SubJoin subjoin) {
        throw new UnsupportedOperationException(subjoin.getClass().getName());
    }

    @Override
    public void visit(CaseExpression caseExpression) {
        throw new UnsupportedOperationException(caseExpression.getClass().getName());
    }

    @Override
    public void visit(WhenClause whenClause) {
        throw new UnsupportedOperationException(whenClause.getClass().getName());
    }

    @Override
    public void visit(ExistsExpression existsExpression) {
        throw new UnsupportedOperationException(existsExpression.getClass().getName());
    }

    @Override
    public void visit(AllComparisonExpression allComparisonExpression) {
        throw new UnsupportedOperationException(allComparisonExpression.getClass().getName());
    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {
        throw new UnsupportedOperationException(anyComparisonExpression.getClass().getName());
    }

    @Override
    public void visit(Concat concat) {
        throw new UnsupportedOperationException(concat.getClass().getName());
    }

    @Override
    public void visit(Matches matches) {
        throw new UnsupportedOperationException(matches.getClass().getName());
    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {
        throw new UnsupportedOperationException(bitwiseAnd.getClass().getName());
    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {
        throw new UnsupportedOperationException(bitwiseOr.getClass().getName());
    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {
        throw new UnsupportedOperationException(bitwiseXor.getClass().getName());
    }

    @Override
    public void visit(OrderByElement orderBy) {
        orderBy.getExpression().accept(this);
    }

    @Override
    public void visit(AllColumns allColumns) {
        throw new UnsupportedOperationException(allColumns.getClass().getName());
    }

    @Override
    public void visit(AllTableColumns allTableColumns) {
        throw new UnsupportedOperationException(allTableColumns.getClass().getName());
    }

    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {
        selectExpressionItem.getExpression().accept(this);
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        final List<Join> joins = plainSelect.getJoins();
        final FromItem fromItem = plainSelect.getFromItem();
        if (joins == null && (fromItem instanceof Table)) {
            //to extract table name if single table used in query
            fromItem.accept(this);
        }

        final Expression where = plainSelect.getWhere();
        if (where != null) {
            where.accept(this);
        }

        final List<SelectItem> selectItems = plainSelect.getSelectItems();
        if (selectItems != null) {
            for (SelectItem selectItem : selectItems) {
                selectItem.accept(this);
            }
        }

        List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
        if (orderByElements != null) {
            for (OrderByElement orderByElement : orderByElements) {
                orderByElement.accept(this);
            }
        }

        fromItem.accept(this);
        if (joins != null) {
            for (Join join : joins) {
                join.getRightItem().accept(this);
            }
        }
    }

    @Override
    public void visit(Union union) {
        throw new UnsupportedOperationException(union.getClass().getName());
    }

    public Map<String, Set<Column>> getColumnsToProject() {
        return columnsToProject;
    }

    public Map<String, String> getAliasNameMap() {
        return aliasNameMap;
    }

    private void visitBinaryExpression(BinaryExpression binaryExpression) {
        binaryExpression.getRightExpression().accept(this);
        binaryExpression.getLeftExpression().accept(this);
    }

    private String searchForColumnInKnownTables(String columnName) {
        TableInfo tableInfo;
        for (String tableName : columnsToProject.keySet()) {
            tableInfo = tablesInfo.get(tableName.toLowerCase());
            if (getColumnIndexInColDefn(tableInfo.getColumnDefinitions(), columnName) != -1) {
                return tableName;
            }
        }
        return null;
    }
}