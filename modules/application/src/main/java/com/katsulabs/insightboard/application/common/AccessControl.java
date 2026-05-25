package com.katsulabs.insightboard.application.common;

import com.katsulabs.insightboard.application.auth.InsightBoardRole;

public interface AccessControl {

    InsightBoardRole requireRole();

    void requireManageUsers();

    void requireWriteDatasourceOrDataset();

    void requireWriteDashboardContent();
}
