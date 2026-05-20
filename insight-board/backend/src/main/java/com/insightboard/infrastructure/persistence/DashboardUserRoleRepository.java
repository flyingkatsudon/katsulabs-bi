package com.insightboard.infrastructure.persistence;

import com.insightboard.domain.metadata.DashboardUserRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardUserRoleRepository extends JpaRepository<DashboardUserRole, Long> {

    List<DashboardUserRole> findByUserId(String userId);

    void deleteByUserIdAndRoleId(String userId, String roleId);
}
