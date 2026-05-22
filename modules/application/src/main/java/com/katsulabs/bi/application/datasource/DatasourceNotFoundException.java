package com.katsulabs.bi.application.datasource;

public class DatasourceNotFoundException extends RuntimeException {

    public DatasourceNotFoundException(long id) {
        super("데이터소스를 찾을 수 없습니다: " + id);
    }
}
