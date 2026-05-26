package com.katsulabs.bi.infrastructure.persistence.mybatis;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardFilterRow {

    private Long filterId;
    private Long boardId;
    private String columnName;
    private String label;
    private String defaultValue;
    private int sortOrder;
    private List<String> optionValues = new ArrayList<>();

    // MyBatis join aggregation 시 null 방지용 커스텀 setter
    public void setOptionValues(List<String> optionValues) {
        this.optionValues = optionValues != null ? optionValues : new ArrayList<>();
    }
}
