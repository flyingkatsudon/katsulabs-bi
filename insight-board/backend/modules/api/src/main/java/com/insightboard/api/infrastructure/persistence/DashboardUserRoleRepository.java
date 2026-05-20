package com.insightboard.api.infrastructure.persistence;

import com.insightboard.api.domain.DashboardUserRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardUserRoleRepository extends JpaRepository<DashboardUserRole, Long> {

    List<DashboardUserRole> findByUserId(String userId);

    void deleteByUserIdAndRoleId(String userId, String roleId);
}
