package com.katsulabs.bi.infrastructure.persistence.mybatis;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatasetRow {

    private Long id;
    private String name;
    private String userId;
    private String userName;
    private String categoryName;
    private Long categoryId;
    private Long datasourceId;
    private String sqlText;
    private boolean platformShared;
    private String data;
    private Timestamp createTime;
    private Timestamp updateTime;
}
