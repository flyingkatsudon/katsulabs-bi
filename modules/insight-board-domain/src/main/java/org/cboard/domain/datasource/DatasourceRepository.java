package org.cboard.domain.datasource;

import java.util.List;
import java.util.Optional;

public interface DatasourceRepository {

    List<DatasourceSummary> findAllSummaries();

    Optional<DatasourceDetail> findById(long datasourceId);

    boolean existsByName(String userId, String name, Long excludeId);

    long insert(String userId, String name, String type, String configJson);

    void update(long id, String name, String configJson);

    void delete(long id);
}
