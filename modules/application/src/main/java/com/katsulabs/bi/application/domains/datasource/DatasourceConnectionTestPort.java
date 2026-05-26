package com.katsulabs.bi.application.domains.datasource;

public interface DatasourceConnectionTestPort {

    void testJdbcConfig(String configJson);
}
