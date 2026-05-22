package com.katsulabs.bi.application.common;

import com.katsulabs.bi.application.auth.KatsulabsBiRole;

public interface AccessControl {

    KatsulabsBiRole requireRole();

    void requireManageUsers();

    void requireWriteDatasourceOrDataset();

    void requireWriteDashboardContent();
}
