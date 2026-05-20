package com.insightboard.api.infrastructure.persistence;

import com.insightboard.api.domain.DashboardRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardRoleRepository extends JpaRepository<DashboardRole, String> {

    List<DashboardRole> findByUserIdOrRoleId(String userId, String roleId);
}
