package com.katsulabs.bi.adapter.web.datasource;

public record DatasourceWriteRequest(String name, String type, String configJson) {
}
