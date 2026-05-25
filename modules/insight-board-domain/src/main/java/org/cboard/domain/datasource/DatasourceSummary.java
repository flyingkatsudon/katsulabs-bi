package org.cboard.domain.datasource;

import java.time.Instant;

public record DatasourceSummary(
        long id,
        String name,
        String type,
        String userId,
        String userName,
        Instant createdAt,
        Instant updatedAt) {
}
