package com.katsulabs.bi.infrastructure.persistence.mybatis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRow {

    private String userId;
    private String loginName;
    private String userName;
    private String userPassword;
    private Integer rbacPolicy;
    private String userStateInfo;
    private String delCd;
    private String roleId;
    private Long defaultBoardId;
}
