package com.katsulabs.insightboard.application.datasource;

public record DatasourceWriteCommand(String name, String type, String configJson) {
}
