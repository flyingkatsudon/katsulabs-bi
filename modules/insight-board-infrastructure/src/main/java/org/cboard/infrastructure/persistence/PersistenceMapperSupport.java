package org.cboard.infrastructure.persistence;

import java.sql.Timestamp;
import java.time.Instant;

final class PersistenceMapperSupport {

    private PersistenceMapperSupport() {
    }

    static Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }
}
