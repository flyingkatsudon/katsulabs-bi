package org.cboard.domain.datasource;

import java.time.Instant;

public record DatasourceDetail(
        long id,
        String name,
        String type,
        String userId,
        String userName,
        String configJson,
        Instant createdAt,
        Instant updatedAt) {
}
