package org.cboard.application.auth;

import java.util.List;

public interface ResourceTypeLoader {

    List<Long> loadByRoleId(String roleId);
}
