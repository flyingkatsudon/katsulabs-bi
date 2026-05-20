package com.insightboard.api.infrastructure.persistence;

import com.insightboard.api.domain.DashboardBoard;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardBoardRepository extends JpaRepository<DashboardBoard, Long> {

    List<DashboardBoard> findByUserIdOrderByBoardIdDesc(String userId);
}
