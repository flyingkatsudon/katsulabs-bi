package com.katsulabs.bi.infrastructure.persistence.mybatis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
