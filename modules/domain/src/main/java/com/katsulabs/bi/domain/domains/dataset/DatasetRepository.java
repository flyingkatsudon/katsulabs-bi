package com.katsulabs.bi.domain.domains.dataset;

import java.util.List;
import java.util.Optional;

public interface DatasetRepository {

    List<DatasetSummary> findAllSummaries();

    Optional<DatasetDetail> findById(long datasetId);

    boolean existsByName(String userId, String name, String categoryName, Long excludeId);

    long insert(String userId, String name, String categoryName, String dataJson);

    void update(long id, String name, String categoryName, String dataJson);

    void delete(long id);
}
