package com.insightboard.api.infrastructure.persistence;

import com.insightboard.api.domain.DashboardDataset;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardDatasetRepository extends JpaRepository<DashboardDataset, Long> {

    List<DashboardDataset> findByUserIdOrderByDatasetNameAsc(String userId);

    List<DashboardDataset> findAllByOrderByDatasetNameAsc();
}
