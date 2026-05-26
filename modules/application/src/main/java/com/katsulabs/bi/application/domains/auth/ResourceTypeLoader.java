package com.katsulabs.bi.application.domains.auth;

import java.util.List;

public interface ResourceTypeLoader {

    List<Long> loadByRoleId(String roleId);
}
