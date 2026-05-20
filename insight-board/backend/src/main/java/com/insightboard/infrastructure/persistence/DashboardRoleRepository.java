package com.insightboard.infrastructure.persistence;

import com.insightboard.domain.metadata.DashboardRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardRoleRepository extends JpaRepository<DashboardRole, String> {

    List<DashboardRole> findByUserIdOrRoleId(String userId, String roleId);
}
