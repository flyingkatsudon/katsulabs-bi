package com.katsulabs.bi.application.domains.datasource;

public record DatasourceWriteCommand(String name, String type, String configJson) {
}
