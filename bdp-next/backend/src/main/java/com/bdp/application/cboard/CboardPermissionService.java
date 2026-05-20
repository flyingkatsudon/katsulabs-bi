package com.bdp.application.cboard;

import com.bdp.domain.metadata.DashboardRoleRes;
import com.bdp.domain.metadata.DashboardUserRole;
import com.bdp.infrastructure.persistence.DashboardRoleResRepository;
import com.bdp.infrastructure.persistence.DashboardUserRoleRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/** 레거시 {@code RolePermission} (permission[0]=edit, [1]=delete). */
@Service
public class CboardPermissionService {

    private final CboardAdminService adminService;
    private final DashboardUserRoleRepository userRoleRepository;
    private final DashboardRoleResRepository roleResRepository;

    public CboardPermissionService(
            CboardAdminService adminService,
            DashboardUserRoleRepository userRoleRepository,
            DashboardRoleResRepository roleResRepository) {
        this.adminService = adminService;
        this.userRoleRepository = userRoleRepository;
        this.roleResRepository = roleResRepository;
    }

    public boolean canEdit(String userId, String ownerId, String resType, Long resId) {
        if (adminService.isAdmin(userId) || userId.equals(ownerId)) {
            return true;
        }
        return hasBit(userId, resType, resId, 0);
    }

    public boolean canDelete(String userId, String ownerId, String resType, Long resId) {
        if (adminService.isAdmin(userId) || userId.equals(ownerId)) {
            return true;
        }
        return hasBit(userId, resType, resId, 1);
    }

    private boolean hasBit(String userId, String resType, Long resId, int bit) {
        List<String> roleIds = userRoleRepository.findByUserId(userId).stream()
                .map(DashboardUserRole::getRoleId)
                .toList();
        return roleResRepository.findAll().stream()
                .filter(r -> roleIds.contains(r.getRoleId()))
                .filter(r -> resType.equals(r.getResType()) && resId.equals(r.getResId()))
                .anyMatch(r -> bitSet(r.getPermission(), bit));
    }

    static boolean bitSet(String permission, int bit) {
        if (permission == null || permission.length() <= bit) {
            return false;
        }
        return permission.charAt(bit) == '1';
    }
}
