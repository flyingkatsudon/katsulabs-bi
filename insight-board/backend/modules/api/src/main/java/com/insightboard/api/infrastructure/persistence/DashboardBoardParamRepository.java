package com.insightboard.api.infrastructure.persistence;

import com.insightboard.api.domain.DashboardBoardParam;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardBoardParamRepository extends JpaRepository<DashboardBoardParam, Long> {

    Optional<DashboardBoardParam> findByBoardIdAndUserId(Long boardId, String userId);
}
