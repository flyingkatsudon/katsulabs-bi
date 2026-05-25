package org.cboard.application.dataset;

public record DatasetWriteCommand(String name, String categoryName, String dataJson) {
}
