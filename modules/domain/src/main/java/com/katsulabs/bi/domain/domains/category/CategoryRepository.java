package com.katsulabs.bi.domain.domains.category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    List<CategorySummary> findAll();

    Optional<CategorySummary> findById(long id);

    boolean existsByName(String name, Long excludeId);

    long insert(String userId, String name);

    void update(long id, String userId, String name);

    void delete(long id);
}
