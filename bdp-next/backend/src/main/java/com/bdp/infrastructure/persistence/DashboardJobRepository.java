package com.bdp.infrastructure.persistence;

import com.bdp.domain.metadata.DashboardJob;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardJobRepository extends JpaRepository<DashboardJob, Long> {

    List<DashboardJob> findByUserIdOrderByJobNameAsc(String userId);
}
