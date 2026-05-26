package com.katsulabs.bi.infrastructure.persistence.mybatis;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardRow {

    private Long id;
    private String name;
    private String userId;
    private String userName;
    private Long categoryId;
    private String categoryName;
    private String layout;
    private Boolean publishedToViewers;
    private String publishedBy;
    private Timestamp createTime;
    private Timestamp updateTime;
}
