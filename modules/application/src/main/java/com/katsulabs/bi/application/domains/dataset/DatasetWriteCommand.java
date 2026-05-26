package com.katsulabs.bi.application.domains.dataset;

public record DatasetWriteCommand(String name, String categoryName, String dataJson) {
}
