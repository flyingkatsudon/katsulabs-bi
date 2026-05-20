package com.bdp.domain.metadata;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dashboard_role_res")
@Getter
@Setter
public class DashboardRoleRes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_res_id")
    private Long roleResId;

    @Column(name = "role_id", length = 100)
    private String roleId;

    @Column(name = "res_type", length = 100)
    private String resType;

    @Column(name = "res_id")
    private Long resId;

    @Column(name = "permission", length = 20)
    private String permission;
}
