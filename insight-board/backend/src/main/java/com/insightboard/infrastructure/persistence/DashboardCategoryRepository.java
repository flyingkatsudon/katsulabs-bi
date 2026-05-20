package com.insightboard.infrastructure.persistence;

import com.insightboard.domain.metadata.DashboardCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardCategoryRepository extends JpaRepository<DashboardCategory, Long> {

    List<DashboardCategory> findAllByOrderByCategoryNameAsc();
}
