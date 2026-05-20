package com.bdp.domain.metadata;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dashboard_user")
@Getter
@Setter
public class DashboardUser {

    @Id
    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(name = "login_name", length = 100)
    private String loginName;

    @Column(name = "user_name", length = 100)
    private String userName;

    @Column(name = "user_password", length = 255)
    private String userPassword;

    @Column(name = "user_status", length = 100)
    private String userStatus;

    @Column(name = "business_code", length = 50)
    private String businessCode;
}
