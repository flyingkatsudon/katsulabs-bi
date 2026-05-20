package com.insightboard.api.cboard;

import com.insightboard.application.cboard.CboardCommonsService;
import com.insightboard.application.cboard.CboardMenuService;
import com.insightboard.domain.metadata.DashboardUser;
import com.insightboard.infrastructure.persistence.DashboardUserRepository;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cboard/commons")
public class CboardCommonsController {

    private final DashboardUserRepository userRepository;
    private final CboardCommonsService commonsService;
    private final CboardMenuService menuService;

    public CboardCommonsController(
            DashboardUserRepository userRepository,
            CboardCommonsService commonsService,
            CboardMenuService menuService) {
        this.userRepository = userRepository;
        this.commonsService = commonsService;
        this.menuService = menuService;
    }

    @RequestMapping("/getUserDetail")
    public Map<String, Object> getUserDetail(@AuthenticationPrincipal String userId) {
        DashboardUser user = userRepository.findById(userId).orElseThrow();
        return Map.of(
                "userId", user.getLoginName() != null ? user.getLoginName() : user.getUserId(),
                "userName", user.getUserName() != null ? user.getUserName() : "",
                "loginName", user.getLoginName() != null ? user.getLoginName() : "",
                "avatar", "/cboard/dist/img/user-male-circle-blue-128.png");
    }

    @RequestMapping("/getMenuList")
    public List<Map<String, Object>> getMenuList(@AuthenticationPrincipal String userId) {
        return menuService.getMenuList(userId);
    }

    @RequestMapping("/changePwd")
    public ServiceStatusDto changePwd(
            @RequestParam String curPwd,
            @RequestParam String newPwd,
            @RequestParam String cfmPwd,
            @AuthenticationPrincipal String userId) {
        return commonsService.changePassword(userId, curPwd, newPwd, cfmPwd);
    }

    @RequestMapping("/persist")
    public String persist(@RequestBody String dataStr) {
        return commonsService.persist(dataStr);
    }
}
