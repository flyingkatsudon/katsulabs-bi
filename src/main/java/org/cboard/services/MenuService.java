package org.cboard.services;

import java.util.ArrayList;
import java.util.List;

import org.cboard.dto.DashboardMenu;
import org.springframework.stereotype.Repository;

/**
 * Created by yfyuan on 2016/12/21.
 */
@Repository
public class MenuService {

    private static List<DashboardMenu> menuList = new ArrayList<>();

    static {
        //menuList.add(new DashboardMenu("cboard", 0, -1, "SIDEBAR.ADMIN", "admin"));
        //menuList.add(new DashboardMenu("cboard", 0,  7, "SIDEBAR.USER_ADMIN", "admin.user"));
        //menuList.add(new DashboardMenu(9, 1, "SIDEBAR.JOB", "config.job"));
        //menuList.add(new DashboardMenu(10, 1, "SIDEBAR.SHARE_RESOURCE", "config.role"));

        menuList.add(new DashboardMenu("bdp", 1, -1, "SIDEBAR.INSIGHT_REPORT", "bdp.insight"));
        menuList.add(new DashboardMenu("bdp", 2, -1, "SIDEBAR.G_ANALYSIS", "bdp.g_analysis"));
        menuList.add(new DashboardMenu("bdp", 3, -1, "SIDEBAR.E_ANALYSIS", "bdp.e_analysis"));
        menuList.add(new DashboardMenu("bdp", 4, -1, "SIDEBAR.ADMIN_PAGE", "admin.user"));
        menuList.add(new DashboardMenu("bdp", 5, -1, "SIDEBAR.ADMIN_PAGE", "admin.user"));
        menuList.add(new DashboardMenu("bdp", 6, -1, "SIDEBAR.ADMIN_PAGE", "admin.user"));
        menuList.add(new DashboardMenu("bdp", 6, -1, "SIDEBAR.CONFIG", "config"));
        menuList.add(new DashboardMenu("bdp", 6,  1, "SIDEBAR.DATA_SOURCE", "config.datasource"));
        menuList.add(new DashboardMenu("bdp", 6,  1, "SIDEBAR.DATASET", "config.dataset"));
        menuList.add(new DashboardMenu("bdp", 6,  1, "SIDEBAR.WIDGET", "config.widget"));
        menuList.add(new DashboardMenu("bdp", 6,  1, "SIDEBAR.DASHBOARD", "config.board"));
        menuList.add(new DashboardMenu("bdp", 6,  1, "SIDEBAR.DASHBOARD_CATEGORY", "config.category"));
        
    }

    public List<DashboardMenu> getMenuList() {
        return menuList;
    }
}
