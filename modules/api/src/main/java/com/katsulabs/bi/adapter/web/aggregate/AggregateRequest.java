package com.katsulabs.bi.adapter.web.aggregate;

import java.util.Map;

public record AggregateRequest(
        Long datasourceId,
        Long datasetId,
        Map<String, String> query,
        String cfg,
        Boolean reload) {
}
