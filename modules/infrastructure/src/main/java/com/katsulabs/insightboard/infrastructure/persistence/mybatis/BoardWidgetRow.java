package com.katsulabs.insightboard.infrastructure.persistence.mybatis;

public class BoardWidgetRow {

    private long boardId;
    private long widgetId;
    private String widgetName;
    private int rowIndex;
    private int columnIndex;
    private int widthCols;
    private Integer heightPx;
    private Integer posX;
    private Integer posY;
    private Integer posEx;
    private Integer posEy;
    private int sortOrder;

    public long getBoardId() {
        return boardId;
    }

    public void setBoardId(long boardId) {
        this.boardId = boardId;
    }

    public long getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(long widgetId) {
        this.widgetId = widgetId;
    }

    public String getWidgetName() {
        return widgetName;
    }

    public void setWidgetName(String widgetName) {
        this.widgetName = widgetName;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public int getWidthCols() {
        return widthCols;
    }

    public void setWidthCols(int widthCols) {
        this.widthCols = widthCols;
    }

    public Integer getHeightPx() {
        return heightPx;
    }

    public void setHeightPx(Integer heightPx) {
        this.heightPx = heightPx;
    }

    public Integer getPosX() {
        return posX;
    }

    public void setPosX(Integer posX) {
        this.posX = posX;
    }

    public Integer getPosY() {
        return posY;
    }

    public void setPosY(Integer posY) {
        this.posY = posY;
    }

    public Integer getPosEx() {
        return posEx;
    }

    public void setPosEx(Integer posEx) {
        this.posEx = posEx;
    }

    public Integer getPosEy() {
        return posEy;
    }

    public void setPosEy(Integer posEy) {
        this.posEy = posEy;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
