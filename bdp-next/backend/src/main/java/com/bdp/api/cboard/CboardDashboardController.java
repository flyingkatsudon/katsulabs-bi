package com.bdp.api.cboard;

import com.bdp.application.cboard.CboardDashboardService;
import com.bdp.domain.metadata.DashboardCategory;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cboard/dashboard")
public class CboardDashboardController {

    private final CboardDashboardService dashboardService;

    public CboardDashboardController(CboardDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/getBoardList")
    public List<Map<String, Object>> getBoardList(@AuthenticationPrincipal String userId) {
        return dashboardService.getBoardList(userId);
    }

    @GetMapping("/getBoardData")
    public Map<String, Object> getBoardData(
            @RequestParam Long id, @AuthenticationPrincipal String userId) {
        return dashboardService.getBoardData(id, userId);
    }

    @GetMapping("/getCategoryList")
    public List<DashboardCategory> getCategoryList() {
        return dashboardService.getCategoryList();
    }

    @GetMapping("/getDatasourceList")
    public List<Map<String, Object>> getDatasourceList(@AuthenticationPrincipal String userId) {
        return dashboardService.getDatasourceList(userId);
    }

    @GetMapping("/getDatasetList")
    public List<Map<String, Object>> getDatasetList(@AuthenticationPrincipal String userId) {
        return dashboardService.getDatasetList(userId);
    }

    @GetMapping("/getAllDatasetList")
    public List<Map<String, Object>> getAllDatasetList() {
        return dashboardService.getAllDatasetList();
    }

    @GetMapping("/getWidgetList")
    public List<Map<String, Object>> getWidgetList(@AuthenticationPrincipal String userId) {
        return dashboardService.getWidgetList(userId);
    }

    @GetMapping("/getWidgetCategoryList")
    public List<String> getWidgetCategoryList() {
        return dashboardService.getWidgetCategoryList();
    }

    @GetMapping("/getDatasetCategoryList")
    public List<String> getDatasetCategoryList() {
        return dashboardService.getDatasetCategoryList();
    }

    @GetMapping("/getProviderList")
    public Set<String> getProviderList() {
        return Set.of("jdbc", "textfile", "h2");
    }

    @GetMapping("/saveNewCategory")
    public ServiceStatusDto saveNewCategory(
            @RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.saveNewCategory(userId, json);
    }

    @GetMapping("/saveNewBoard")
    public ServiceStatusDto saveNewBoard(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.saveNewBoard(userId, json);
    }

    @GetMapping("/deleteBoard")
    public ServiceStatusDto deleteBoard(@RequestParam Long id, @AuthenticationPrincipal String userId) {
        return dashboardService.deleteBoard(userId, id);
    }
}
