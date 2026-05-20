package com.bdp.application.cboard;

import com.bdp.domain.metadata.DashboardRole;
import com.bdp.domain.metadata.DashboardRoleRes;
import com.bdp.domain.metadata.DashboardUser;
import com.bdp.domain.metadata.DashboardUserRole;
import com.bdp.infrastructure.persistence.DashboardRoleRepository;
import com.bdp.infrastructure.persistence.DashboardRoleResRepository;
import com.bdp.infrastructure.persistence.DashboardUserRepository;
import com.bdp.infrastructure.persistence.DashboardUserRoleRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CboardAdminService {

    private static final String ADMIN_USER_ID = "1";
    private static final String ADMIN_ROLE_ID = "ADMIN";

    private final DashboardUserRepository userRepository;
    private final DashboardRoleRepository roleRepository;
    private final DashboardUserRoleRepository userRoleRepository;
    private final DashboardRoleResRepository roleResRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public CboardAdminService(
            DashboardUserRepository userRepository,
            DashboardRoleRepository roleRepository,
            DashboardUserRoleRepository userRoleRepository,
            DashboardRoleResRepository roleResRepository,
            PasswordEncoder passwordEncoder,
            ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleResRepository = roleResRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }

    public boolean isAdmin(String userId) {
        if (ADMIN_USER_ID.equals(userId)) {
            return true;
        }
        return userRoleRepository.findByUserId(userId).stream()
                .anyMatch(ur -> ADMIN_ROLE_ID.equals(ur.getRoleId()));
    }

    public boolean isConfig(String userId, String type) {
        return isAdmin(userId);
    }

    public List<DashboardUser> getUserList() {
        return userRepository.findAll();
    }

    public String saveNewUser(String userJson) {
        try {
            Map<String, Object> body = objectMapper.readValue(userJson, new TypeReference<>() {});
            String uid = str(body.get("userId"));
            if (uid == null || uid.isBlank()) {
                uid = UUID.randomUUID().toString();
            }
            DashboardUser u = new DashboardUser();
            u.setUserId(uid);
            u.setLoginName(str(body.getOrDefault("loginName", uid)));
            u.setUserName(str(body.getOrDefault("userName", uid)));
            String pwd = str(body.get("userPassword"));
            u.setUserPassword(passwordEncoder.encode(pwd != null ? pwd : "changeme"));
            u.setUserStatus("ACTIVE");
            userRepository.save(u);
            return "1";
        } catch (Exception e) {
            return "0";
        }
    }

    public String updateUser(String userJson) {
        try {
            Map<String, Object> body = objectMapper.readValue(userJson, new TypeReference<>() {});
            DashboardUser u = userRepository.findById(str(body.get("userId"))).orElseThrow();
            if (body.get("loginName") != null) {
                u.setLoginName(str(body.get("loginName")));
            }
            if (body.get("userName") != null) {
                u.setUserName(str(body.get("userName")));
            }
            if (body.get("userPassword") != null && !str(body.get("userPassword")).isBlank()) {
                u.setUserPassword(passwordEncoder.encode(str(body.get("userPassword"))));
            }
            userRepository.save(u);
            return "1";
        } catch (Exception e) {
            return "0";
        }
    }

    public String deleteUser(String userId) {
        userRepository.deleteById(userId);
        return "1";
    }

    public List<DashboardRole> getRoleList(String userId) {
        if (isAdmin(userId)) {
            return roleRepository.findAll();
        }
        return roleRepository.findAll().stream()
                .filter(r -> userId.equals(r.getUserId()))
                .toList();
    }

    public List<DashboardRole> getRoleListAll() {
        return roleRepository.findAll();
    }

    public String saveRole(String json) {
        try {
            Map<String, Object> body = objectMapper.readValue(json, new TypeReference<>() {});
            DashboardRole r = new DashboardRole();
            r.setRoleId(str(body.get("roleId")));
            r.setRoleName(str(body.get("roleName")));
            r.setUserId(str(body.get("userId")));
            roleRepository.save(r);
            return "1";
        } catch (Exception e) {
            return "0";
        }
    }

    public String updateRole(String json) {
        try {
            Map<String, Object> body = objectMapper.readValue(json, new TypeReference<>() {});
            DashboardRole r = roleRepository.findById(str(body.get("roleId"))).orElseThrow();
            r.setRoleName(str(body.get("roleName")));
            r.setUserId(str(body.get("userId")));
            roleRepository.save(r);
            return "1";
        } catch (Exception e) {
            return "0";
        }
    }

    @Transactional
    public String deleteRole(String roleId) {
        roleResRepository.deleteByRoleId(roleId);
        roleRepository.deleteById(roleId);
        return "1";
    }

    public List<DashboardUserRole> getUserRoleList() {
        return userRoleRepository.findAll();
    }

    @Transactional
    public String updateUserRole(String json) {
        try {
            Map<String, Object> body = objectMapper.readValue(json, new TypeReference<>() {});
            String userId = str(body.get("userId"));
            String roleId = str(body.get("roleId"));
            userRoleRepository.deleteByUserIdAndRoleId(userId, roleId);
            DashboardUserRole ur = new DashboardUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            userRoleRepository.save(ur);
            return "1";
        } catch (Exception e) {
            return "0";
        }
    }

    public String deleteUserRole(String userId, String roleId) {
        userRoleRepository.deleteByUserIdAndRoleId(userId, roleId);
        return "1";
    }

    public List<DashboardRoleRes> getRoleResList() {
        return roleResRepository.findAll();
    }

    @Transactional
    public String updateRoleRes(String json) {
        try {
            Map<String, Object> body = objectMapper.readValue(json, new TypeReference<>() {});
            String roleId = str(body.get("roleId"));
            roleResRepository.deleteByRoleId(roleId);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resList = (List<Map<String, Object>>) body.get("resList");
            if (resList != null) {
                for (Map<String, Object> res : resList) {
                    DashboardRoleRes rr = new DashboardRoleRes();
                    rr.setRoleId(roleId);
                    rr.setResType(str(res.get("resType")));
                    rr.setResId(Long.valueOf(res.get("resId").toString()));
                    rr.setPermission(str(res.getOrDefault("permission", "read")));
                    roleResRepository.save(rr);
                }
            }
            return "1";
        } catch (Exception e) {
            return "0";
        }
    }

    public String updateRoleResUser(String[] roleIds, String resIdArr) {
        return "1";
    }

    private static String str(Object o) {
        return o == null ? null : String.valueOf(o);
    }
}
