package com.insightboard.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dashboard_role")
@Getter
@Setter
public class DashboardRole {

    @Id
    @Column(name = "role_id", length = 100)
    private String roleId;

    @Column(name = "role_name", length = 100)
    private String roleName;

    @Column(name = "user_id", length = 50)
    private String userId;
}
