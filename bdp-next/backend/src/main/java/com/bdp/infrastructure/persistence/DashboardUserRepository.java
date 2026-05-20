package com.bdp.infrastructure.persistence;

import com.bdp.domain.metadata.DashboardUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardUserRepository extends JpaRepository<DashboardUser, String> {

    Optional<DashboardUser> findByLoginName(String loginName);
}
