package com.katsulabs.bi.infrastructure.persistence.mybatis;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatasourceRow {

    private Long id;
    private String name;
    private String type;
    private String userId;
    private String userName;
    private String config;
    private Timestamp createTime;
    private Timestamp updateTime;
}
