package com.bdp.api.cboard;

import com.bdp.application.cboard.CboardDashboardService;
import com.bdp.application.cboard.MockAggregateService;
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
    private final MockAggregateService aggregateService;

    public CboardDashboardController(
            CboardDashboardService dashboardService, MockAggregateService aggregateService) {
        this.dashboardService = dashboardService;
        this.aggregateService = aggregateService;
    }

    @GetMapping("/getBoardList")
    public List<Map<String, Object>> getBoardList(@AuthenticationPrincipal String userId) {
        return dashboardService.getBoardList(userId);
    }

    @GetMapping("/getBoardData")
    public Map<String, Object> getBoardData(@RequestParam Long id, @AuthenticationPrincipal String userId) {
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

    @GetMapping("/getConfigParams")
    public List<Map<String, Object>> getConfigParams(
            @RequestParam String type, @RequestParam(defaultValue = "datasource") String page) {
        return dashboardService.getConfigParams(type, page);
    }

    @GetMapping("/getConfigView")
    public String getConfigView(
            @RequestParam String type, @RequestParam(defaultValue = "datasource") String page) {
        return dashboardService.getConfigView(type, page);
    }

    @GetMapping("/getAggregateData")
    public Map<String, Object> getAggregateData(
            @RequestParam(required = false) Long datasetId,
            @RequestParam(required = false) String cfg) {
        return aggregateService.aggregate(datasetId);
    }

    @GetMapping("/saveNewCategory")
    public ServiceStatusDto saveNewCategory(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.saveNewCategory(userId, json);
    }

    @GetMapping("/updateCategory")
    public ServiceStatusDto updateCategory(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.updateCategory(userId, json);
    }

    @GetMapping("/deleteCategory")
    public String deleteCategory(@RequestParam Long id) {
        return dashboardService.deleteCategory(id);
    }

    @GetMapping("/saveNewBoard")
    public ServiceStatusDto saveNewBoard(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.saveNewBoard(userId, json);
    }

    @GetMapping("/updateBoard")
    public ServiceStatusDto updateBoard(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.updateBoard(userId, json);
    }

    @GetMapping("/deleteBoard")
    public ServiceStatusDto deleteBoard(@RequestParam Long id, @AuthenticationPrincipal String userId) {
        return dashboardService.deleteBoard(userId, id);
    }

    @GetMapping("/saveNewWidget")
    public ServiceStatusDto saveNewWidget(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.saveNewWidget(userId, json);
    }

    @GetMapping("/updateWidget")
    public ServiceStatusDto updateWidget(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.updateWidget(userId, json);
    }

    @GetMapping("/deleteWidget")
    public ServiceStatusDto deleteWidget(@RequestParam Long id, @AuthenticationPrincipal String userId) {
        return dashboardService.deleteWidget(userId, id);
    }

    @GetMapping("/saveNewDataset")
    public ServiceStatusDto saveNewDataset(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.saveNewDataset(userId, json);
    }

    @GetMapping("/updateDataset")
    public ServiceStatusDto updateDataset(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.updateDataset(userId, json);
    }

    @GetMapping("/deleteDataset")
    public ServiceStatusDto deleteDataset(@RequestParam Long id, @AuthenticationPrincipal String userId) {
        return dashboardService.deleteDataset(userId, id);
    }

    @GetMapping("/saveNewDatasource")
    public ServiceStatusDto saveNewDatasource(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.saveNewDatasource(userId, json);
    }

    @GetMapping("/updateDatasource")
    public ServiceStatusDto updateDatasource(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.updateDatasource(userId, json);
    }

    @GetMapping("/deleteDatasource")
    public ServiceStatusDto deleteDatasource(@RequestParam Long id, @AuthenticationPrincipal String userId) {
        return dashboardService.deleteDatasource(userId, id);
    }
}
