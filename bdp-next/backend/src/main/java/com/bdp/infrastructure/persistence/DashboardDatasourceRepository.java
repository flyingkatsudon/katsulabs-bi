package com.bdp.infrastructure.persistence;

import com.bdp.domain.metadata.DashboardDatasource;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardDatasourceRepository extends JpaRepository<DashboardDatasource, Long> {

    List<DashboardDatasource> findByUserIdOrderBySourceNameAsc(String userId);
}
