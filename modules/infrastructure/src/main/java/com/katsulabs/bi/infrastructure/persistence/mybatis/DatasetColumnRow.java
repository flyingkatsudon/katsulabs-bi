package com.katsulabs.bi.infrastructure.persistence.mybatis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatasetColumnRow {

    private Long id;
    private Long datasetId;
    private String columnName;
    private String kind;
    private String alias;
    private int sortOrder;
}
