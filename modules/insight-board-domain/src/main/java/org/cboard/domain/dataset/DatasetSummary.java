package org.cboard.domain.dataset;

import java.time.Instant;

public record DatasetSummary(
        long id,
        String name,
        String userId,
        String userName,
        String categoryName,
        Long datasourceId,
        Instant createdAt,
        Instant updatedAt) {
}
