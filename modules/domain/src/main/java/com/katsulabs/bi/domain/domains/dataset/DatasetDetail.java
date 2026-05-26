package com.katsulabs.bi.domain.domains.dataset;

import java.time.Instant;

public record DatasetDetail(
        long id,
        String name,
        String userId,
        String userName,
        String categoryName,
        String dataJson,
        Instant createdAt,
        Instant updatedAt) {
}
