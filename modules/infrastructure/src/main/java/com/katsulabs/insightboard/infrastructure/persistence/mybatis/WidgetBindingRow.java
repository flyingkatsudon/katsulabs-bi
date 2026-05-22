package com.katsulabs.insightboard.infrastructure.persistence.mybatis;

public class WidgetBindingRow {
    private Long id;
    private Long widgetId;
    private String axis;
    private String columnName;
    private String alias;
    private String aggregateFn;
    private int sortOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(Long widgetId) {
        this.widgetId = widgetId;
    }

    public String getAxis() {
        return axis;
    }

    public void setAxis(String axis) {
        this.axis = axis;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAggregateFn() {
        return aggregateFn;
    }

    public void setAggregateFn(String aggregateFn) {
        this.aggregateFn = aggregateFn;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
