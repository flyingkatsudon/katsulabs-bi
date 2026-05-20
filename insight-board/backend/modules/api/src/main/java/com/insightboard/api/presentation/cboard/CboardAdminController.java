package com.insightboard.api.presentation.cboard;

import com.insightboard.api.application.cboard.CboardAdminService;
import com.insightboard.api.application.cboard.CboardDashboardService;
import com.insightboard.api.application.cboard.CboardMenuService;
import com.insightboard.api.domain.DashboardRole;
import com.insightboard.api.domain.DashboardRoleRes;
import com.insightboard.api.domain.DashboardUser;
import com.insightboard.api.domain.DashboardUserRole;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cboard/admin")
public class CboardAdminController {

    private final CboardAdminService adminService;
    private final CboardDashboardService dashboardService;
    private final CboardMenuService menuService;

    public CboardAdminController(
            CboardAdminService adminService,
            CboardDashboardService dashboardService,
            CboardMenuService menuService) {
        this.adminService = adminService;
        this.dashboardService = dashboardService;
        this.menuService = menuService;
    }

    @RequestMapping("/isAdmin")
    public boolean isAdmin(@AuthenticationPrincipal String userId) {
        return adminService.isAdmin(userId);
    }

    @RequestMapping("/isConfig")
    public boolean isConfig(@RequestParam String type, @AuthenticationPrincipal String userId) {
        return adminService.isConfig(userId, type);
    }

    @RequestMapping("/getUserList")
    public List<DashboardUser> getUserList() {
        return adminService.getUserList();
    }

    @RequestMapping("/saveNewUser")
    public String saveNewUser(@RequestParam String user) {
        return adminService.saveNewUser(user);
    }

    @RequestMapping("/updateUser")
    public String updateUser(@RequestParam String user) {
        return adminService.updateUser(user);
    }

    @RequestMapping("/deleteUser")
    public String deleteUser(@RequestParam String userId) {
        return adminService.deleteUser(userId);
    }

    @RequestMapping("/getRoleList")
    public List<DashboardRole> getRoleList(@AuthenticationPrincipal String userId) {
        return adminService.getRoleList(userId);
    }

    @RequestMapping("/getRoleListAll")
    public List<DashboardRole> getRoleListAll() {
        return adminService.getRoleListAll();
    }

    @RequestMapping("/saveRole")
    public String saveRole(@RequestParam String json) {
        return adminService.saveRole(json);
    }

    @RequestMapping("/updateRole")
    public String updateRole(@RequestParam String json) {
        return adminService.updateRole(json);
    }

    @RequestMapping("/deleteRole")
    public String deleteRole(@RequestParam String roleId) {
        return adminService.deleteRole(roleId);
    }

    @RequestMapping("/getUserRoleList")
    public List<DashboardUserRole> getUserRoleList() {
        return adminService.getUserRoleList();
    }

    @RequestMapping("/updateUserRole")
    public String updateUserRole(@RequestParam String json) {
        return adminService.updateUserRole(json);
    }

    @RequestMapping("/deleteUserRole")
    public String deleteUserRole(@RequestParam String userId, @RequestParam String roleId) {
        return adminService.deleteUserRole(userId, roleId);
    }

    @RequestMapping("/getRoleResList")
    public List<DashboardRoleRes> getRoleResList() {
        return adminService.getRoleResList();
    }

    @RequestMapping("/updateRoleRes")
    public String updateRoleRes(@RequestParam String json) {
        return adminService.updateRoleRes(json);
    }

    @RequestMapping("/updateRoleResUser")
    public String updateRoleResUser(@RequestParam String roleIdArr, @RequestParam String resIdArr) {
        return adminService.updateRoleResUser(roleIdArr.split(","), resIdArr);
    }

    @RequestMapping("/getBoardList")
    public List<Map<String, Object>> getBoardList(@AuthenticationPrincipal String userId) {
        return dashboardService.getBoardList(userId);
    }

    @RequestMapping("/getBoardListUser")
    public List<Map<String, Object>> getBoardListUser(@AuthenticationPrincipal String userId) {
        return dashboardService.getBoardList(userId);
    }

    @RequestMapping("/getWidgetList")
    public List<Map<String, Object>> getWidgetList(@AuthenticationPrincipal String userId) {
        return dashboardService.getAllWidgetList(userId);
    }

    @RequestMapping("/getWidgetListUser")
    public List<Map<String, Object>> getWidgetListUser(@AuthenticationPrincipal String userId) {
        return dashboardService.getWidgetList(userId);
    }

    @RequestMapping("/getDatasetList")
    public List<Map<String, Object>> getDatasetList(@AuthenticationPrincipal String userId) {
        return dashboardService.getAllDatasetList(userId);
    }

    @RequestMapping("/getDatasetListUser")
    public List<Map<String, Object>> getDatasetListUser(@AuthenticationPrincipal String userId) {
        return dashboardService.getDatasetList(userId);
    }

    @RequestMapping("/getDatasourceList")
    public List<Map<String, Object>> getDatasourceList(@AuthenticationPrincipal String userId) {
        return dashboardService.getDatasourceList(userId);
    }

    @RequestMapping("/getMenuList")
    public List<Map<String, Object>> getMenuList(@AuthenticationPrincipal String userId) {
        return menuService.getMenuList(userId);
    }
}
