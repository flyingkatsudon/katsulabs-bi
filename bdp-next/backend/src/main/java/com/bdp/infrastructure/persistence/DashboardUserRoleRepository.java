package com.bdp.infrastructure.persistence;

import com.bdp.domain.metadata.DashboardUserRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardUserRoleRepository extends JpaRepository<DashboardUserRole, Long> {

    List<DashboardUserRole> findByUserId(String userId);

    void deleteByUserIdAndRoleId(String userId, String roleId);
}
