package com.katsulabs.bi.adapter.web.domains.datasource;

public record DatasourceWriteRequest(String name, String type, String configJson) {
}
