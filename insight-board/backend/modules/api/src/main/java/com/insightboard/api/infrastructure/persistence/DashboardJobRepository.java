package com.insightboard.api.infrastructure.persistence;

import com.insightboard.api.domain.DashboardJob;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardJobRepository extends JpaRepository<DashboardJob, Long> {

    List<DashboardJob> findByUserIdOrderByJobNameAsc(String userId);
}
