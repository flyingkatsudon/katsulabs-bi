package org.cboard.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.cboard.dao.BoardDao;
import org.cboard.dao.DatasetDao;
import org.cboard.dao.DatasourceDao;
import org.cboard.dao.JobDao;
import org.cboard.dao.MenuDao;
import org.cboard.dao.RoleDao;
import org.cboard.dao.UserDao;
import org.cboard.dao.WidgetDao;
import org.cboard.dto.DashboardMenu;
import org.cboard.dto.User;
import org.cboard.dto.ViewDashboardBoard;
import org.cboard.dto.ViewDashboardDataset;
import org.cboard.dto.ViewDashboardDatasource;
import org.cboard.dto.ViewDashboardJob;
import org.cboard.dto.ViewDashboardRoleRes;
import org.cboard.dto.ViewDashboardWidget;
import org.cboard.pojo.DashboardBoard;
import org.cboard.pojo.DashboardDataset;
import org.cboard.pojo.DashboardRole;
import org.cboard.pojo.DashboardRoleRes;
import org.cboard.pojo.DashboardUser;
import org.cboard.pojo.DashboardUserRole;
import org.cboard.pojo.DashboardWidget;
import org.cboard.services.AdminSerivce;
import org.cboard.services.DatasourceService;
import org.cboard.services.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;

/**
 * Created by yfyuan on 2016/12/2.
 */
@RestController
@RequestMapping("/cboard/admin")
public class AdminController extends BaseController {

    @Autowired
    private AdminSerivce adminSerivce;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Value("${admin_user_id}")
    private String adminUserId;

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private BoardDao boardDao;
    @Autowired
    private DatasetDao datasetDao;
    @Autowired
    private DatasourceDao datasourceDao;
    @Autowired
    private JobDao jobDao;
    @Autowired
    private WidgetDao widgetDao;
    @Autowired
    private MenuService menuService;
    @Autowired
    private DatasourceService datasourceService;

    @RequestMapping(value = "/saveNewUser")
    public String saveNewUser(@RequestParam(name = "user") String user) {
        JSONObject jsonObject = JSONObject.parseObject(user);
        //return adminSerivce.addUser(UUID.randomUUID().toString(), jsonObject.getString("loginName"), jsonObject.getString("userName"), jsonObject.getString("userPassword"));
        
        String roleId = jsonObject.getString("v4") + '-' + jsonObject.getString("v5") + '-' + jsonObject.getString("v6");
        return adminSerivce.addUser(jsonObject.getString("userId"), jsonObject.getString("userId"), jsonObject.getString("userName"), jsonObject.getString("userPassword"), roleId);
    }

    @RequestMapping(value = "/updateUser")
    public String updateUser(@RequestParam(name = "user") String user) {
        JSONObject jsonObject = JSONObject.parseObject(user);
        return adminSerivce.updateUser(jsonObject.getString("userId"), jsonObject.getString("loginName"), jsonObject.getString("userName"), jsonObject.getString("userPassword"));
    }

    @RequestMapping(value = "/deleteUser")
    public String deleteUser(@RequestParam(name = "userId") String userId) {
        return adminSerivce.deleteUser(userId);
    }

    @RequestMapping(value = "/getUserList")
    public List<DashboardUser> getUserList() {
        List<DashboardUser> list = userDao.getUserList();
        return list;
    }

    @RequestMapping(value = "/saveRole")
    public String saveRole(@RequestParam(name = "role") String role) {
        JSONObject jsonObject = JSONObject.parseObject(role);
        return adminSerivce.addRole(UUID.randomUUID().toString(), jsonObject.getString("roleName"), jsonObject.getString("userId"));
    }

    @RequestMapping(value = "/updateRole")
    public String updateRole(@RequestParam(name = "role") String role) {
        JSONObject jsonObject = JSONObject.parseObject(role);
        return adminSerivce.updateRole(jsonObject.getString("roleId"), jsonObject.getString("roleName"), jsonObject.getString("userId"));
    }

    @RequestMapping(value = "/deleteRole")
    public String deleteRole(@RequestParam(name = "roleId") String roleId) {
        return adminSerivce.deleteRole(roleId);
    }

    @RequestMapping(value = "/getRoleList")
    public List<DashboardRole> getRoleList() {
        List<DashboardRole> list = roleDao.getRoleList(user.getUserId());
        return list;
    }

    @RequestMapping(value = "/getRoleListAll")
    public List<DashboardRole> getRoleListAll() {
        List<DashboardRole> list = roleDao.getRoleListAll();
        return list;
    }

    @RequestMapping(value = "/updateUserRole")
    public String updateUserRole(@RequestParam(name = "userIdArr") String userIdArr, @RequestParam(name = "roleIdArr") String roleIdArr) {
        return adminSerivce.updateUserRole(
                JSONArray.parseArray(userIdArr).toArray(new String[]{}),
                JSONArray.parseArray(roleIdArr).toArray(new String[]{}),
                user.getUserId()
        );
    }

    @RequestMapping(value = "/deleteUserRole")
    public String deleteUserRole(@RequestParam(name = "userIdArr") String userIdArr, @RequestParam(name = "roleIdArr") String roleIdArr) {
        return adminSerivce.deleteUserRoles(
                JSONArray.parseArray(userIdArr).toArray(new String[]{}),
                JSONArray.parseArray(roleIdArr).toArray(new String[]{}),
                user.getUserId()
        );
    }

    @RequestMapping(value = "/getUserRoleList")
    public List<DashboardUserRole> getUserRoleList() {
        List<DashboardUserRole> list = userDao.getUserRoleList();
        return list;
    }

    @RequestMapping(value = "/getRoleResList")
    public List<ViewDashboardRoleRes> getRoleResList() {
        List<DashboardRoleRes> list = roleDao.getRoleResList();
        return list.stream().map(ViewDashboardRoleRes::new).collect(Collectors.toList());
    }

    @RequestMapping(value = "/updateRoleRes")
    public String updateRoleRes(@RequestParam(name = "roleIdArr") String roleIdArr, @RequestParam(name = "resIdArr") String resIdArr) {
        return adminSerivce.updateRoleRes(JSONArray.parseArray(roleIdArr).toArray(new String[]{}), resIdArr);
    }

    @RequestMapping(value = "/updateRoleResUser")
    public String updateRoleResUser(@RequestParam(name = "roleIdArr") String roleIdArr, @RequestParam(name = "resIdArr") String resIdArr) {
        return adminSerivce.updateRoleResUser(JSONArray.parseArray(roleIdArr).toArray(new String[]{}), resIdArr);
    }

    @RequestMapping(value = "/isAdmin")
    public boolean isAdmin() {
        return adminUserId.equals(user.getUserId());
    }

    @RequestMapping(value = "/isConfig")
    public boolean isConfig(@RequestParam(name = "type") String type) {
        /*if (user.getUserId().equals(adminUserId)) {
            return true;
        } else if (type.equals("widget")) {
            List<Long> menuIdList = menuDao.getMenuIdByUserRole(user.getUserId());
            if (menuIdList.contains(1L) && menuIdList.contains(4L)) {
                return true;
            }
        }
        return false;*/
    	return true;
    }

    @RequestMapping(value = "/getDatasetList")
    public List<ViewDashboardDataset> getDatasetList() {
        List<DashboardDataset> list = datasetDao.getDatasetListAdmin(user.getUserId());
        return Lists.transform(list, ViewDashboardDataset.TO);
    }

    @RequestMapping(value = "/getDatasetListUser")
    public List<ViewDashboardDataset> getDatasetListUser() {
        List<DashboardDataset> list = adminSerivce.getDatasetList(user.getUserId());
        return Lists.transform(list, ViewDashboardDataset.TO);
    }

    @RequestMapping(value = "/getJobList")
    public List<ViewDashboardJob> getJobList() {
        return jobDao.getJobListAdmin(user.getUserId()).stream().map(ViewDashboardJob::new).collect(Collectors.toList());
    }

    @RequestMapping(value = "/getBoardList")
    public List<ViewDashboardBoard> getBoardList() {
    	System.out.println("Admin CTRL");
        List<DashboardBoard> list = boardDao.getBoardListAdmin(user.getUserId());
        return Lists.transform(list, ViewDashboardBoard.TO);
    }

    @RequestMapping(value = "/getBoardListUser")
    public List<ViewDashboardBoard> getBoardListUser() {
        List<DashboardBoard> list = adminSerivce.getBoardList(user.getUserId());
        return Lists.transform(list, ViewDashboardBoard.TO);
    }

    @RequestMapping(value = "/getWidgetList")
    public List<ViewDashboardWidget> getWidgetList() {
        List<DashboardWidget> list = widgetDao.getWidgetListAdmin(user.getUserId());
        return Lists.transform(list, ViewDashboardWidget.TO);
    }

    @RequestMapping(value = "/getWidgetListUser")
    public List<ViewDashboardWidget> getWidgetListUser() {
        List<DashboardWidget> list = adminSerivce.getWidgetList(user.getUserId());
        return Lists.transform(list, ViewDashboardWidget.TO);
    }

    @RequestMapping(value = "/getDatasourceList")
    public List<ViewDashboardDatasource> getDatasourceList() {
        return datasourceService.getViewDatasourceList(() -> datasourceDao.getDatasourceListAdmin(user.getUserId()));
    }

    @RequestMapping(value = "/getMenuList")
    public List<DashboardMenu> getMenuList() {
        if (adminUserId.equals(user.getUserId())) {
            return menuService.getMenuList();
        } else {
            List<Long> menuId = menuDao.getMenuIdByRoleAdmin(user.getUserId());
            return menuService.getMenuList().stream().filter(e -> menuId.stream().anyMatch(id -> id.equals(e.getMenuId()))).collect(Collectors.toList());
        }
    }
}
