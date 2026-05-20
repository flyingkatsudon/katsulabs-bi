package com.insightboard.api.infrastructure.persistence;

import com.insightboard.api.domain.DashboardCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardCategoryRepository extends JpaRepository<DashboardCategory, Long> {

    List<DashboardCategory> findAllByOrderByCategoryNameAsc();
}
