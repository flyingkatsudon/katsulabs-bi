package com.katsulabs.bi.application.datasource;

public record DatasourceWriteCommand(String name, String type, String configJson) {
}
