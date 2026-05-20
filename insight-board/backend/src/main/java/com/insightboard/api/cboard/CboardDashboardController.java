package com.insightboard.api.cboard;

import com.insightboard.application.cboard.CboardDashboardService;
import com.insightboard.application.cboard.CboardDataProviderService;
import com.insightboard.application.cboard.CboardExportService;
import com.insightboard.application.cboard.CboardJobService;
import com.insightboard.application.cboard.CboardQueryParams;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.insightboard.application.cboard.dto.AggregateResultDto;
import com.insightboard.application.cboard.dto.DataProviderResultDto;
import com.insightboard.domain.metadata.DashboardBoardParam;
import com.insightboard.domain.metadata.DashboardCategory;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 레거시 CBoard {@code DashboardController} 호환 API.
 * {@code @RequestMapping} — GET/POST 모두 허용 (AngularJS $http.post 호환).
 */
@RestController
@RequestMapping("/cboard/dashboard")
public class CboardDashboardController {

    private final CboardDashboardService dashboardService;
    private final CboardDataProviderService dataProviderService;
    private final CboardJobService jobService;
    private final CboardExportService exportService;
    private final ObjectMapper objectMapper;

    public CboardDashboardController(
            CboardDashboardService dashboardService,
            CboardDataProviderService dataProviderService,
            CboardJobService jobService,
            CboardExportService exportService,
            ObjectMapper objectMapper) {
        this.dashboardService = dashboardService;
        this.dataProviderService = dataProviderService;
        this.jobService = jobService;
        this.exportService = exportService;
        this.objectMapper = objectMapper;
    }

    @RequestMapping("/getBoardList")
    public List<Map<String, Object>> getBoardList(@AuthenticationPrincipal String userId) {
        return dashboardService.getBoardList(userId);
    }

    @RequestMapping("/getBoardData")
    public Map<String, Object> getBoardData(@RequestParam Long id, @AuthenticationPrincipal String userId) {
        return dashboardService.getBoardData(id, userId);
    }

    @RequestMapping("/getCategoryList")
    public List<DashboardCategory> getCategoryList() {
        return dashboardService.getCategoryList();
    }

    @RequestMapping("/getDatasourceList")
    public List<Map<String, Object>> getDatasourceList(@AuthenticationPrincipal String userId) {
        return dashboardService.getDatasourceList(userId);
    }

    @RequestMapping("/getDatasetList")
    public List<Map<String, Object>> getDatasetList(@AuthenticationPrincipal String userId) {
        return dashboardService.getDatasetList(userId);
    }

    @RequestMapping("/getAllDatasetList")
    public List<Map<String, Object>> getAllDatasetList(@AuthenticationPrincipal String userId) {
        return dashboardService.getAllDatasetList(userId);
    }

    @RequestMapping("/getWidgetList")
    public List<Map<String, Object>> getWidgetList(@AuthenticationPrincipal String userId) {
        return dashboardService.getWidgetList(userId);
    }

    @RequestMapping("/getAllWidgetList")
    public List<Map<String, Object>> getAllWidgetList(@AuthenticationPrincipal String userId) {
        return dashboardService.getAllWidgetList(userId);
    }

    @RequestMapping("/getWidgetCategoryList")
    public List<String> getWidgetCategoryList() {
        return dashboardService.getWidgetCategoryList();
    }

    @RequestMapping("/getDatasetCategoryList")
    public List<String> getDatasetCategoryList() {
        return dashboardService.getDatasetCategoryList();
    }

    @RequestMapping("/getProviderList")
    public Set<String> getProviderList() {
        return Set.of("jdbc", "textfile", "h2");
    }

    @RequestMapping("/getConfigParams")
    public List<Map<String, Object>> getConfigParams(
            @RequestParam String type,
            @RequestParam(defaultValue = "datasource") String page,
            @RequestParam(required = false) Long datasourceId) {
        return dashboardService.getConfigParams(type, page, datasourceId);
    }

    @RequestMapping("/getConfigView")
    public String getConfigView(
            @RequestParam String type,
            @RequestParam(defaultValue = "datasource") String page,
            @RequestParam(required = false) Long datasourceId) {
        return dashboardService.getConfigView(type, page, datasourceId);
    }

    @RequestMapping("/getDatasourceParams")
    public List<Map<String, Object>> getDatasourceParams(@RequestParam String type) {
        return dataProviderService.getDatasourceParams(type);
    }

    @RequestMapping("/getDatasourceView")
    public String getDatasourceView(@RequestParam String type) {
        return dataProviderService.getDatasourceView(type);
    }

    @RequestMapping("/getAggregateData")
    public AggregateResultDto getAggregateData(
            @RequestParam(required = false) Long datasourceId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long datasetId,
            @RequestParam(required = false) String cfg,
            @RequestParam(required = false, defaultValue = "false") Boolean reload) {
        return dataProviderService.queryAggData(
                datasourceId, datasetId, cfg, CboardQueryParams.parse(query, objectMapper));
    }

    @RequestMapping("/viewAggDataQuery")
    public String[] viewAggDataQuery(
            @RequestParam(required = false) Long datasourceId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long datasetId,
            @RequestParam(required = false) String cfg) {
        String sql = dataProviderService.viewAggDataQuery(
                datasourceId, datasetId, cfg, CboardQueryParams.parse(query, objectMapper));
        return new String[] {sql};
    }

    @RequestMapping("/getColumns")
    public DataProviderResultDto getColumns(
            @RequestParam(required = false) Long datasourceId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long datasetId,
            @RequestParam(required = false, defaultValue = "false") Boolean reload) {
        return dataProviderService.getColumns(
                datasourceId, datasetId, CboardQueryParams.parse(query, objectMapper));
    }

    @RequestMapping("/getDimensionValues")
    public String[] getDimensionValues(
            @RequestParam(required = false) Long datasourceId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long datasetId,
            @RequestParam(name = "colmunName") String columnName,
            @RequestParam(required = false) String cfg,
            @RequestParam(required = false, defaultValue = "false") Boolean reload) {
        return dataProviderService.getDimensionValues(
                datasourceId,
                datasetId,
                columnName,
                cfg,
                CboardQueryParams.parse(query, objectMapper));
    }

    @RequestMapping("/test")
    public ServiceStatusDto test(
            @RequestParam(required = false) String datasource, @RequestParam(required = false) String query) {
        return dataProviderService.testDatasource(datasource, query);
    }

    @RequestMapping("/dashboardWidget")
    public Map<String, Object> dashboardWidget(@RequestParam Long id, @AuthenticationPrincipal String userId) {
        return dashboardService.getDashboardWidget(id, userId);
    }

    @RequestMapping("/checkWidget")
    public ServiceStatusDto checkWidget(@RequestParam Long id) {
        return dashboardService.checkWidget(id);
    }

    @RequestMapping("/checkDatasource")
    public ServiceStatusDto checkDatasource(@RequestParam Long id) {
        return dashboardService.checkDatasource(id);
    }

    @RequestMapping("/getBoardParam")
    public DashboardBoardParam getBoardParam(
            @RequestParam Long boardId, @AuthenticationPrincipal String userId) {
        return dashboardService.getBoardParam(boardId, userId);
    }

    @RequestMapping("/saveBoardParam")
    public String saveBoardParam(
            @RequestParam Long boardId,
            @RequestParam String config,
            @AuthenticationPrincipal String userId) {
        return dashboardService.saveBoardParam(boardId, userId, config);
    }

    @RequestMapping("/saveNewCategory")
    public ServiceStatusDto saveNewCategory(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.saveNewCategory(userId, json);
    }

    @RequestMapping("/updateCategory")
    public ServiceStatusDto updateCategory(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.updateCategory(userId, json);
    }

    @RequestMapping("/deleteCategory")
    public String deleteCategory(@RequestParam Long id) {
        return dashboardService.deleteCategory(id);
    }

    @RequestMapping("/saveNewBoard")
    public ServiceStatusDto saveNewBoard(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.saveNewBoard(userId, json);
    }

    @RequestMapping("/updateBoard")
    public ServiceStatusDto updateBoard(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.updateBoard(userId, json);
    }

    @RequestMapping("/deleteBoard")
    public ServiceStatusDto deleteBoard(@RequestParam Long id, @AuthenticationPrincipal String userId) {
        return dashboardService.deleteBoard(userId, id);
    }

    @RequestMapping("/saveNewWidget")
    public ServiceStatusDto saveNewWidget(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.saveNewWidget(userId, json);
    }

    @RequestMapping("/updateWidget")
    public ServiceStatusDto updateWidget(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.updateWidget(userId, json);
    }

    @RequestMapping("/deleteWidget")
    public ServiceStatusDto deleteWidget(@RequestParam Long id, @AuthenticationPrincipal String userId) {
        return dashboardService.deleteWidget(userId, id);
    }

    @RequestMapping("/saveNewDataset")
    public ServiceStatusDto saveNewDataset(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.saveNewDataset(userId, json);
    }

    @RequestMapping("/updateDataset")
    public ServiceStatusDto updateDataset(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.updateDataset(userId, json);
    }

    @RequestMapping("/deleteDataset")
    public ServiceStatusDto deleteDataset(@RequestParam Long id, @AuthenticationPrincipal String userId) {
        return dashboardService.deleteDataset(userId, id);
    }

    @RequestMapping("/saveNewDatasource")
    public ServiceStatusDto saveNewDatasource(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.saveNewDatasource(userId, json);
    }

    @RequestMapping("/updateDatasource")
    public ServiceStatusDto updateDatasource(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return dashboardService.updateDatasource(userId, json);
    }

    @RequestMapping("/deleteDatasource")
    public ServiceStatusDto deleteDatasource(@RequestParam Long id, @AuthenticationPrincipal String userId) {
        return dashboardService.deleteDatasource(userId, id);
    }

    @RequestMapping("/getJobList")
    public List<Map<String, Object>> getJobList(@AuthenticationPrincipal String userId) {
        return jobService.getJobList(userId);
    }

    @RequestMapping("/saveJob")
    public ServiceStatusDto saveJob(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return jobService.save(userId, json);
    }

    @RequestMapping("/updateJob")
    public ServiceStatusDto updateJob(@RequestParam String json, @AuthenticationPrincipal String userId) {
        return jobService.update(userId, json);
    }

    @RequestMapping("/deleteJob")
    public ServiceStatusDto deleteJob(@RequestParam Long id, @AuthenticationPrincipal String userId) {
        return jobService.delete(userId, id);
    }

    @RequestMapping("/execJob")
    public ServiceStatusDto execJob(@RequestParam Long id, @AuthenticationPrincipal String userId) {
        return jobService.exec(userId, id);
    }

    @RequestMapping("/getJobStatus")
    public Map<String, Object> getJobStatus(@RequestParam Long id) {
        return jobService.getJobStatus(id);
    }

    @RequestMapping("/exportBoard")
    public ResponseEntity<byte[]> exportBoard(@RequestParam Long id, @AuthenticationPrincipal String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "report.xls");
        return new ResponseEntity<>(exportService.exportBoard(id, userId), headers, HttpStatus.OK);
    }

    @RequestMapping("/tableToxls")
    public ResponseEntity<byte[]> tableToxls(@RequestParam String data) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "table.xls");
        return new ResponseEntity<>(exportService.tableToXls(data), headers, HttpStatus.OK);
    }
}
