package org.cboard.domain.board;

import java.time.Instant;

public record BoardSummary(
        long id,
        String name,
        String userId,
        String userName,
        Long categoryId,
        String categoryName,
        Instant createdAt,
        Instant updatedAt) {
}
