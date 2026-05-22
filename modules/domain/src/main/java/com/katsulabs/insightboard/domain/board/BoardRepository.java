package com.katsulabs.insightboard.domain.board;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {

    List<BoardSummary> findAllSummaries(boolean publishedOnly);

    Optional<BoardDetail> findById(long boardId);

    boolean existsByName(String userId, String name, Long excludeId);

    long insert(String userId, String name, Long categoryId, String layoutJson);

    void update(long id, String name, Long categoryId, String layoutJson);

    void delete(long id);
}
