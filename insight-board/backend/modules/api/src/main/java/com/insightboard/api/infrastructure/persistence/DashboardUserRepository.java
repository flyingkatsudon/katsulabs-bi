package com.insightboard.api.infrastructure.persistence;

import com.insightboard.api.domain.DashboardUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardUserRepository extends JpaRepository<DashboardUser, String> {

    Optional<DashboardUser> findByLoginName(String loginName);
}
