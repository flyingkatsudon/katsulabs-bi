package com.katsulabs.bi.adapter.web.domains.aggregate;

import java.util.Map;

public record AggregateRequest(
        Long datasourceId,
        Long datasetId,
        Map<String, String> query,
        String cfg,
        Boolean reload) {
}
