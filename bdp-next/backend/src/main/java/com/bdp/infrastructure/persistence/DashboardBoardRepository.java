package com.bdp.infrastructure.persistence;

import com.bdp.domain.metadata.DashboardBoard;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardBoardRepository extends JpaRepository<DashboardBoard, Long> {

    List<DashboardBoard> findByUserIdOrderByBoardIdDesc(String userId);
}
