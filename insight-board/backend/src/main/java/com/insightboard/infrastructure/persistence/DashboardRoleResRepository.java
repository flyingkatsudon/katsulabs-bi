package com.insightboard.infrastructure.persistence;

import com.insightboard.domain.metadata.DashboardRoleRes;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardRoleResRepository extends JpaRepository<DashboardRoleRes, Long> {

    List<DashboardRoleRes> findByRoleId(String roleId);

    void deleteByRoleId(String roleId);
}
