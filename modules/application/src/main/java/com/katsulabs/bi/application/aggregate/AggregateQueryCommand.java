package com.katsulabs.bi.application.aggregate;

import java.util.Map;

public record AggregateQueryCommand(
        Long datasourceId,
        Long datasetId,
        Map<String, String> queryParams,
        String configJson,
        boolean reload) {
}
