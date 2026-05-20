package com.insightboard.infrastructure.persistence;

import com.insightboard.domain.metadata.DashboardBoard;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardBoardRepository extends JpaRepository<DashboardBoard, Long> {

    List<DashboardBoard> findByUserIdOrderByBoardIdDesc(String userId);
}
