package com.katsulabs.bi.adapter.web.dataset;

import java.util.List;
import java.util.Map;

public record DatasetPreviewResponse(List<String> columns, List<Map<String, Object>> rows) {
}
