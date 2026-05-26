package com.katsulabs.bi.infrastructure.persistence.mybatis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSummaryRow {

    private String userId;
    private String loginName;
    private String userName;
    private String roleId;
    private String roleName;
    private String userStatus;
    private String delCd;
}
