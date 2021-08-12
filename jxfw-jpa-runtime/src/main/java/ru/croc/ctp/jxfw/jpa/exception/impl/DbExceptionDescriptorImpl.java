package ru.croc.ctp.jxfw.jpa.exception.impl;

import static com.google.common.collect.Lists.newArrayList;

import ru.croc.ctp.jxfw.jpa.exception.DbExceptionDescriptor;
import ru.croc.ctp.jxfw.jpa.exception.XfwViolationType;

import java.util.List;
import javax.annotation.Nullable;

/**
 * Дефолтная имплементация.
 */
public class DbExceptionDescriptorImpl implements DbExceptionDescriptor {

    private String constraint;
    private String tableName;
    private String details;
    private List<String> columnNames = newArrayList();
    private XfwViolationType xfwViolationType;
    private Throwable cause;

    @Nullable
    @Override
    public String getConstraint() {
        return constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    @Nullable
    @Override
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public XfwViolationType getViolationType() {
        return xfwViolationType;
    }

    public void setXfwViolationType(XfwViolationType xfwViolationType) {
        this.xfwViolationType = xfwViolationType;
    }
}
