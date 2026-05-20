package com.bdp.infrastructure.persistence;

import com.bdp.domain.metadata.DashboardRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardRoleRepository extends JpaRepository<DashboardRole, String> {

    List<DashboardRole> findByUserIdOrRoleId(String userId, String roleId);
}
