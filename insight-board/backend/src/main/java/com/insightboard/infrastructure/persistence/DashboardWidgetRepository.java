package com.insightboard.infrastructure.persistence;

import com.insightboard.domain.metadata.DashboardWidget;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardWidgetRepository extends JpaRepository<DashboardWidget, Long> {

    List<DashboardWidget> findByUserIdOrderByWidgetNameAsc(String userId);

    List<DashboardWidget> findAllByOrderByWidgetNameAsc();
}
