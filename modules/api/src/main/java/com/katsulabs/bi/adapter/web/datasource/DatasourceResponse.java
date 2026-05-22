package com.katsulabs.bi.adapter.web.datasource;

import java.time.Instant;

public record DatasourceResponse(
        long id,
        String name,
        String type,
        String userId,
        String userName,
        String configJson,
        Instant createdAt,
        Instant updatedAt) {
}
