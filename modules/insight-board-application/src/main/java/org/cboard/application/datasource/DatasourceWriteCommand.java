package org.cboard.application.datasource;

public record DatasourceWriteCommand(String name, String type, String configJson) {
}
