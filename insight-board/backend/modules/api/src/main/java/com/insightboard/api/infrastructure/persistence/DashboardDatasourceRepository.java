package com.insightboard.api.infrastructure.persistence;

import com.insightboard.api.domain.DashboardDatasource;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardDatasourceRepository extends JpaRepository<DashboardDatasource, Long> {

    List<DashboardDatasource> findByUserIdOrderBySourceNameAsc(String userId);
}
