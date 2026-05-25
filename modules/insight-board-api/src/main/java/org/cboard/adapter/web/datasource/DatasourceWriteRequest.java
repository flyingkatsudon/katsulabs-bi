package org.cboard.adapter.web.datasource;

public record DatasourceWriteRequest(String name, String type, String configJson) {
}
