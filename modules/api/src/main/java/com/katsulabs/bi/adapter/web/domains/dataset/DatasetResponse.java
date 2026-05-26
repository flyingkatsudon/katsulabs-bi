package com.katsulabs.bi.adapter.web.domains.dataset;

import java.time.Instant;

public record DatasetResponse(
        long id,
        String name,
        String userId,
        String userName,
        String categoryName,
        Long datasourceId,
        String dataJson,
        Instant createdAt,
        Instant updatedAt) {
}
