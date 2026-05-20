package com.insightboard.api.application.cboard;

import com.insightboard.api.domain.DashboardRoleRes;
import com.insightboard.api.domain.DashboardUserRole;
import com.insightboard.api.infrastructure.persistence.DashboardRoleResRepository;
import com.insightboard.api.infrastructure.persistence.DashboardUserRoleRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/** 레거시 {@code MenuService} 메뉴 정의 + 역할 기반 필터. */
@Service
public class CboardMenuService {

    record MenuDef(long menuId, long parentId, String menuName, String menuCode) {}

    private static final List<MenuDef> ALL_MENUS = List.of(
            new MenuDef(4, -1, "SIDEBAR.ADMIN_PAGE", "admin.user"),
            new MenuDef(6, -1, "SIDEBAR.CONFIG", "config"),
            new MenuDef(7, 6, "SIDEBAR.DATA_SOURCE", "config.datasource"),
            new MenuDef(8, 6, "SIDEBAR.DATASET", "config.dataset"),
            new MenuDef(9, 6, "SIDEBAR.WIDGET", "config.widget"),
            new MenuDef(10, 6, "SIDEBAR.DASHBOARD", "config.board"),
            new MenuDef(11, 6, "SIDEBAR.DASHBOARD_CATEGORY", "config.category"),
            new MenuDef(12, 6, "SIDEBAR.JOB", "config.job"));

    private final CboardAdminService adminService;
    private final DashboardUserRoleRepository userRoleRepository;
    private final DashboardRoleResRepository roleResRepository;

    public CboardMenuService(
            CboardAdminService adminService,
            DashboardUserRoleRepository userRoleRepository,
            DashboardRoleResRepository roleResRepository) {
        this.adminService = adminService;
        this.userRoleRepository = userRoleRepository;
        this.roleResRepository = roleResRepository;
    }

    public List<Map<String, Object>> getMenuList(String userId) {
        Set<Long> allowed = allowedMenuIds(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (MenuDef m : ALL_MENUS) {
            if (allowed.contains(m.menuId())) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("menuId", m.menuId());
                row.put("parentId", m.parentId());
                row.put("menuName", m.menuName());
                row.put("menuCode", m.menuCode());
                result.add(row);
            }
        }
        return result;
    }

    public Set<Long> allowedMenuIds(String userId) {
        if (adminService.isAdmin(userId)) {
            return ALL_MENUS.stream().map(MenuDef::menuId).collect(Collectors.toSet());
        }
        List<String> roleIds = userRoleRepository.findByUserId(userId).stream()
                .map(DashboardUserRole::getRoleId)
                .toList();
        return roleResRepository.findAll().stream()
                .filter(r -> roleIds.contains(r.getRoleId()))
                .filter(r -> "menu".equals(r.getResType()))
                .map(DashboardRoleRes::getResId)
                .collect(Collectors.toSet());
    }
}
