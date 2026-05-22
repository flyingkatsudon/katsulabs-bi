package com.katsulabs.bi.application.dataset;

import java.util.List;
import java.util.Map;

public record DatasetPreviewResult(List<String> columns, List<Map<String, Object>> rows) {
}
