package com.katsulabs.bi.infrastructure.common.persistence;

import java.sql.Timestamp;
import java.time.Instant;

public final class PersistenceMapperSupport {

    private PersistenceMapperSupport() {
    }

    public static Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }
}
