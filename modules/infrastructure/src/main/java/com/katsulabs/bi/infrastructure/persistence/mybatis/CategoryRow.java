package com.katsulabs.bi.infrastructure.persistence.mybatis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRow {

    private long id;
    private String name;
    private String userId;
}
