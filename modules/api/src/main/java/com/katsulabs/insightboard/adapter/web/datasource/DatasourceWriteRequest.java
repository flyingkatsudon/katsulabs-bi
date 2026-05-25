package com.katsulabs.insightboard.adapter.web.datasource;

public record DatasourceWriteRequest(String name, String type, String configJson) {
}
