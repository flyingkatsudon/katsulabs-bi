package com.katsulabs.insightboard.application.dataset;

public record DatasetWriteCommand(String name, String categoryName, String dataJson) {
}
