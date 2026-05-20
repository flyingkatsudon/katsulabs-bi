package com.bdp.api.cboard;

import com.bdp.domain.metadata.DashboardUser;
import com.bdp.infrastructure.persistence.DashboardUserRepository;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cboard/commons")
public class CboardCommonsController {

    private final DashboardUserRepository userRepository;

    public CboardCommonsController(DashboardUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/getUserDetail")
    public Map<String, Object> getUserDetail(@AuthenticationPrincipal String userId) {
        DashboardUser user = userRepository.findById(userId).orElseThrow();
        return Map.of(
                "userId", user.getLoginName() != null ? user.getLoginName() : user.getUserId(),
                "userName", user.getUserName() != null ? user.getUserName() : "",
                "loginName", user.getLoginName() != null ? user.getLoginName() : "",
                "avatar", "/cboard/dist/img/user-male-circle-blue-128.png");
    }

    @GetMapping("/getMenuList")
    public List<Map<String, Object>> getMenuList() {
        return List.of(
                menu("bdp.insight", "SIDEBAR.INSIGHT_REPORT"),
                menu("bdp.g_analysis", "SIDEBAR.G_ANALYSIS"),
                menu("bdp.e_analysis", "SIDEBAR.E_ANALYSIS"),
                menu("admin.user", "SIDEBAR.ADMIN_PAGE"),
                menu("config", "SIDEBAR.CONFIG"),
                menu("config.datasource", "SIDEBAR.DATA_SOURCE"),
                menu("config.dataset", "SIDEBAR.DATASET"),
                menu("config.widget", "SIDEBAR.WIDGET"),
                menu("config.board", "SIDEBAR.DASHBOARD"),
                menu("config.category", "SIDEBAR.DASHBOARD_CATEGORY"));
    }

    private static Map<String, Object> menu(String code, String name) {
        return Map.of("menuCode", code, "menuName", name);
    }
}
