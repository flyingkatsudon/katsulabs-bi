package com.katsulabs.bi.infrastructure.persistence.mybatis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WidgetBindingRow {

    private Long id;
    private Long widgetId;
    private String axis;
    private String columnName;
    private String alias;
    private String aggregateFn;
    private int sortOrder;
}
