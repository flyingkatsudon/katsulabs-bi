package com.insightboard.api.application.cboard;

import com.insightboard.api.application.cboard.dto.ServiceStatusDto;
import com.insightboard.api.domain.DashboardBoard;
import com.insightboard.api.domain.DashboardCategory;
import com.insightboard.api.domain.DashboardDataset;
import com.insightboard.api.domain.DashboardDatasource;
import com.insightboard.api.domain.DashboardWidget;
import com.insightboard.api.domain.DashboardBoardParam;
import com.insightboard.api.infrastructure.persistence.DashboardBoardParamRepository;
import com.insightboard.api.infrastructure.persistence.DashboardBoardRepository;
import com.insightboard.api.infrastructure.persistence.DashboardCategoryRepository;
import com.insightboard.api.infrastructure.persistence.DashboardDatasetRepository;
import com.insightboard.api.infrastructure.persistence.DashboardDatasourceRepository;
import com.insightboard.api.infrastructure.persistence.DashboardWidgetRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CboardDashboardService {

    private final DashboardBoardRepository boardRepository;
    private final DashboardCategoryRepository categoryRepository;
    private final DashboardWidgetRepository widgetRepository;
    private final DashboardDatasetRepository datasetRepository;
    private final DashboardDatasourceRepository datasourceRepository;
    private final DashboardBoardParamRepository boardParamRepository;
    private final CboardPermissionService permissionService;
    private final ObjectMapper objectMapper;

    public CboardDashboardService(
            DashboardBoardRepository boardRepository,
            DashboardCategoryRepository categoryRepository,
            DashboardWidgetRepository widgetRepository,
            DashboardDatasetRepository datasetRepository,
            DashboardDatasourceRepository datasourceRepository,
            DashboardBoardParamRepository boardParamRepository,
            CboardPermissionService permissionService,
            ObjectMapper objectMapper) {
        this.boardRepository = boardRepository;
        this.categoryRepository = categoryRepository;
        this.widgetRepository = widgetRepository;
        this.datasetRepository = datasetRepository;
        this.datasourceRepository = datasourceRepository;
        this.boardParamRepository = boardParamRepository;
        this.permissionService = permissionService;
        this.objectMapper = objectMapper;
    }

    public List<Map<String, Object>> getBoardList(String userId) {
        return boardRepository.findAll().stream().map(b -> toBoardView(b, userId)).toList();
    }

    public Map<String, Object> getBoardData(Long id, String userId) {
        DashboardBoard board = boardRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found"));
        Map<String, Object> view = toBoardView(board, userId);
        view.put("layout", hydrateLayout(board.getLayoutJson(), userId));
        return view;
    }

    public Map<String, Object> getDashboardWidget(Long widgetId, String userId) {
        DashboardWidget widget = widgetRepository
                .findById(widgetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Widget not found"));
        return toWidgetView(widget, userId);
    }

    public List<Map<String, Object>> getAllWidgetList(String userId) {
        return widgetRepository.findAll().stream().map(w -> toWidgetView(w, userId)).toList();
    }

    public ServiceStatusDto checkWidget(Long id) {
        long datasets = datasetRepository.findAll().stream()
                .filter(d -> d.getDataJson() != null && d.getDataJson().contains("\"widgetId\":" + id))
                .count();
        if (datasets > 0) {
            return ServiceStatusDto.fail("Widget is referenced by datasets");
        }
        return ServiceStatusDto.ok();
    }

    public ServiceStatusDto checkDatasource(Long id) {
        long datasets = datasetRepository.findAll().stream()
                .filter(d -> d.getDataJson() != null && d.getDataJson().contains("\"datasource\":" + id))
                .count();
        if (datasets > 0) {
            return ServiceStatusDto.fail("Datasource is referenced by datasets");
        }
        return ServiceStatusDto.ok();
    }

    public DashboardBoardParam getBoardParam(Long boardId, String userId) {
        return boardParamRepository
                .findByBoardIdAndUserId(boardId, userId)
                .orElseGet(() -> {
                    DashboardBoardParam p = new DashboardBoardParam();
                    p.setBoardId(boardId);
                    p.setUserId(userId);
                    p.setConfig("{}");
                    return p;
                });
    }

    public String saveBoardParam(Long boardId, String userId, String config) {
        DashboardBoardParam param = boardParamRepository
                .findByBoardIdAndUserId(boardId, userId)
                .orElseGet(DashboardBoardParam::new);
        param.setBoardId(boardId);
        param.setUserId(userId);
        param.setConfig(config);
        boardParamRepository.save(param);
        return "1";
    }

    public List<DashboardCategory> getCategoryList() {
        return categoryRepository.findAllByOrderByCategoryNameAsc();
    }

    public List<Map<String, Object>> getDatasourceList(String userId) {
        return datasourceRepository.findByUserIdOrderBySourceNameAsc(userId).stream()
                .map(ds -> toDatasourceView(ds, userId))
                .toList();
    }

    public List<Map<String, Object>> getDatasetList(String userId) {
        return datasetRepository.findByUserIdOrderByDatasetNameAsc(userId).stream()
                .map(d -> toDatasetView(d, userId))
                .toList();
    }

    public List<Map<String, Object>> getAllDatasetList(String userId) {
        return datasetRepository.findAllByOrderByDatasetNameAsc().stream()
                .map(d -> toDatasetView(d, userId))
                .toList();
    }

    public List<Map<String, Object>> getWidgetList(String userId) {
        return widgetRepository.findByUserIdOrderByWidgetNameAsc(userId).stream()
                .map(w -> toWidgetView(w, userId))
                .toList();
    }

    public List<String> getWidgetCategoryList() {
        return widgetRepository.findAll().stream()
                .map(DashboardWidget::getCategoryName)
                .filter(n -> n != null && !n.isBlank())
                .distinct()
                .sorted()
                .toList();
    }

    public List<String> getDatasetCategoryList() {
        return datasetRepository.findAll().stream()
                .map(DashboardDataset::getCategoryName)
                .filter(n -> n != null && !n.isBlank())
                .distinct()
                .sorted()
                .toList();
    }

    public ServiceStatusDto saveNewCategory(String userId, String json) {
        try {
            Map<String, Object> body = objectMapper.readValue(json, new TypeReference<>() {});
            DashboardCategory c = new DashboardCategory();
            c.setCategoryName((String) body.get("name"));
            c.setUserId(userId);
            categoryRepository.save(c);
            return ServiceStatusDto.ok();
        } catch (Exception e) {
            return ServiceStatusDto.fail(e.getMessage());
        }
    }

    public ServiceStatusDto saveNewBoard(String userId, String json) {
        try {
            Map<String, Object> body = objectMapper.readValue(json, new TypeReference<>() {});
            DashboardBoard b = new DashboardBoard();
            b.setUserId(userId);
            b.setBoardName((String) body.get("name"));
            if (body.get("categoryId") != null) {
                b.setCategoryId(Long.valueOf(body.get("categoryId").toString()));
            }
            if (body.get("layout") != null) {
                b.setLayoutJson(objectMapper.writeValueAsString(body.get("layout")));
            }
            boardRepository.save(b);
            return ServiceStatusDto.ok();
        } catch (Exception e) {
            return ServiceStatusDto.fail(e.getMessage());
        }
    }

    public ServiceStatusDto deleteBoard(String userId, Long id) {
        Optional<DashboardBoard> board = boardRepository.findById(id);
        if (board.isEmpty()) {
            return ServiceStatusDto.fail("Not found");
        }
        boardRepository.deleteById(id);
        return ServiceStatusDto.ok();
    }

    public ServiceStatusDto updateBoard(String userId, String json) {
        try {
            Map<String, Object> body = objectMapper.readValue(json, new TypeReference<>() {});
            Long id = Long.valueOf(body.get("id").toString());
            DashboardBoard b = boardRepository.findById(id).orElseThrow();
            if (body.get("name") != null) {
                b.setBoardName((String) body.get("name"));
            }
            if (body.get("categoryId") != null) {
                b.setCategoryId(Long.valueOf(body.get("categoryId").toString()));
            }
            if (body.get("layout") != null) {
                b.setLayoutJson(objectMapper.writeValueAsString(body.get("layout")));
            }
            boardRepository.save(b);
            return ServiceStatusDto.ok();
        } catch (Exception e) {
            return ServiceStatusDto.fail(e.getMessage());
        }
    }

    public ServiceStatusDto updateCategory(String userId, String json) {
        try {
            Map<String, Object> body = objectMapper.readValue(json, new TypeReference<>() {});
            Long id = Long.valueOf(body.get("id").toString());
            DashboardCategory c = categoryRepository.findById(id).orElseThrow();
            c.setCategoryName((String) body.get("name"));
            categoryRepository.save(c);
            return ServiceStatusDto.ok();
        } catch (Exception e) {
            return ServiceStatusDto.fail(e.getMessage());
        }
    }

    public String deleteCategory(Long id) {
        categoryRepository.deleteById(id);
        return "1";
    }

    public ServiceStatusDto saveNewWidget(String userId, String json) {
        return saveWidget(userId, json, null);
    }

    public ServiceStatusDto updateWidget(String userId, String json) {
        try {
            Map<String, Object> body = objectMapper.readValue(json, new TypeReference<>() {});
            Long id = Long.valueOf(body.get("id").toString());
            return saveWidget(userId, json, id);
        } catch (Exception e) {
            return ServiceStatusDto.fail(e.getMessage());
        }
    }

    public ServiceStatusDto deleteWidget(String userId, Long id) {
        widgetRepository.deleteById(id);
        return ServiceStatusDto.ok();
    }

    public ServiceStatusDto saveNewDataset(String userId, String json) {
        return saveDataset(userId, json, null);
    }

    public ServiceStatusDto updateDataset(String userId, String json) {
        try {
            Map<String, Object> body = objectMapper.readValue(json, new TypeReference<>() {});
            Long id = Long.valueOf(body.get("id").toString());
            return saveDataset(userId, json, id);
        } catch (Exception e) {
            return ServiceStatusDto.fail(e.getMessage());
        }
    }

    public ServiceStatusDto deleteDataset(String userId, Long id) {
        datasetRepository.deleteById(id);
        return ServiceStatusDto.ok();
    }

    public ServiceStatusDto saveNewDatasource(String userId, String json) {
        return saveDatasource(userId, json, null);
    }

    public ServiceStatusDto updateDatasource(String userId, String json) {
        try {
            Map<String, Object> body = objectMapper.readValue(json, new TypeReference<>() {});
            Long id = Long.valueOf(body.get("id").toString());
            return saveDatasource(userId, json, id);
        } catch (Exception e) {
            return ServiceStatusDto.fail(e.getMessage());
        }
    }

    public ServiceStatusDto deleteDatasource(String userId, Long id) {
        datasourceRepository.deleteById(id);
        return ServiceStatusDto.ok();
    }

    public List<Map<String, Object>> getConfigParams(String type, String page, Long datasourceId) {
        return List.of(
                Map.of("name", "table", "label", "Table / query", "type", "input"),
                Map.of("name", "sql", "label", "SQL", "type", "textarea"));
    }

    public String getConfigView(String type, String page, Long datasourceId) {
        return "<div class=\"form-group\"><label>SQL</label><textarea class=\"form-control\" name=\"sql\" rows=\"4\"></textarea></div>";
    }

    private ServiceStatusDto saveWidget(String userId, String json, Long existingId) {
        try {
            Map<String, Object> body = objectMapper.readValue(json, new TypeReference<>() {});
            DashboardWidget w = existingId != null
                    ? widgetRepository.findById(existingId).orElseThrow()
                    : new DashboardWidget();
            w.setUserId(userId);
            w.setWidgetName((String) body.get("name"));
            w.setCategoryName((String) body.getOrDefault("categoryName", "default"));
            w.setDataJson(stringifyDataField(body.get("data")));
            widgetRepository.save(w);
            return ServiceStatusDto.ok();
        } catch (Exception e) {
            return ServiceStatusDto.fail(e.getMessage());
        }
    }

    private ServiceStatusDto saveDataset(String userId, String json, Long existingId) {
        try {
            Map<String, Object> body = objectMapper.readValue(json, new TypeReference<>() {});
            DashboardDataset d = existingId != null
                    ? datasetRepository.findById(existingId).orElseThrow()
                    : new DashboardDataset();
            d.setUserId(userId);
            d.setDatasetName((String) body.get("name"));
            d.setCategoryName((String) body.getOrDefault("categoryName", "default"));
            d.setDataJson(stringifyDataField(body.get("data")));
            datasetRepository.save(d);
            return ServiceStatusDto.ok();
        } catch (Exception e) {
            return ServiceStatusDto.fail(e.getMessage());
        }
    }

    private ServiceStatusDto saveDatasource(String userId, String json, Long existingId) {
        try {
            Map<String, Object> body = objectMapper.readValue(json, new TypeReference<>() {});
            DashboardDatasource d = existingId != null
                    ? datasourceRepository.findById(existingId).orElseThrow()
                    : new DashboardDatasource();
            d.setUserId(userId);
            d.setSourceName((String) body.get("name"));
            d.setSourceType((String) body.getOrDefault("type", "jdbc"));
            d.setConfig(stringifyDataField(body.get("config")));
            datasourceRepository.save(d);
            return ServiceStatusDto.ok();
        } catch (Exception e) {
            return ServiceStatusDto.fail(e.getMessage());
        }
    }

    private String stringifyDataField(Object data) throws Exception {
        if (data == null) {
            return "{}";
        }
        if (data instanceof String s) {
            return s;
        }
        return objectMapper.writeValueAsString(data);
    }

    private Map<String, Object> toBoardView(DashboardBoard board, String userId) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", board.getBoardId());
        m.put("userId", board.getUserId());
        m.put("categoryId", board.getCategoryId());
        m.put("name", board.getBoardName());
        m.put("userName", "Administrator");
        m.put("loginName", "admin");
        m.put("edit", permissionService.canEdit(userId, board.getUserId(), "board", board.getBoardId()));
        m.put("delete", permissionService.canDelete(userId, board.getUserId(), "board", board.getBoardId()));
        if (board.getCategoryId() != null) {
            categoryRepository.findById(board.getCategoryId()).ifPresent(c -> m.put("categoryName", c.getCategoryName()));
        }
        return m;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> hydrateLayout(String layoutJson, String userId) {
        Map<String, Object> layout = (Map<String, Object>) parseJson(layoutJson, defaultLayout());
        Object rowsObj = layout.get("rows");
        if (!(rowsObj instanceof List<?> rows)) {
            return layout;
        }
        boolean containsParam = false;
        List<Map<String, Object>> newRows = new ArrayList<>();
        for (Object rowObj : rows) {
            if (!(rowObj instanceof Map<?, ?> rowMap)) {
                continue;
            }
            Map<String, Object> row = new LinkedHashMap<>();
            rowMap.forEach((k, v) -> row.put(String.valueOf(k), v));
            if ("param".equals(row.get("type"))) {
                containsParam = true;
                newRows.add(row);
                continue;
            }
            Object widgetsObj = row.get("widgets");
            if (widgetsObj instanceof List<?> widgets) {
                List<Map<String, Object>> newWidgets = new ArrayList<>();
                for (Object wObj : widgets) {
                    if (!(wObj instanceof Map<?, ?> wMap)) {
                        continue;
                    }
                    Map<String, Object> cell = new LinkedHashMap<>();
                    wMap.forEach((k, v) -> cell.put(String.valueOf(k), v));
                    Long widgetId = resolveWidgetId(cell);
                    if (widgetId != null) {
                        widgetRepository.findById(widgetId).ifPresent(w -> {
                            cell.put("widget", toWidgetView(w, userId));
                            cell.putIfAbsent("widgetId", widgetId);
                        });
                    }
                    cell.putIfAbsent("show", Boolean.TRUE);
                    newWidgets.add(cell);
                }
                row.put("widgets", newWidgets);
            }
            newRows.add(row);
        }
        layout.put("rows", newRows);
        if (containsParam) {
            layout.put("containsParam", true);
        }
        return layout;
    }

    private Long resolveWidgetId(Map<String, Object> cell) {
        Object widgetId = cell.get("widgetId");
        if (widgetId instanceof Number n) {
            return n.longValue();
        }
        Object widget = cell.get("widget");
        if (widget instanceof Map<?, ?> wm) {
            Object id = wm.get("id");
            if (id instanceof Number n) {
                return n.longValue();
            }
        }
        return null;
    }

    private Map<String, Object> defaultLayout() {
        Map<String, Object> layout = new LinkedHashMap<>();
        layout.put("type", "grid");
        layout.put("rows", new ArrayList<>());
        return layout;
    }

    private Map<String, Object> toDatasourceView(DashboardDatasource ds, String userId) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", ds.getDatasourceId());
        m.put("name", ds.getSourceName());
        m.put("type", ds.getSourceType());
        m.put("config", parseJson(ds.getConfig(), Map.of()));
        m.put("edit", permissionService.canEdit(userId, ds.getUserId(), "datasource", ds.getDatasourceId()));
        m.put("delete", permissionService.canDelete(userId, ds.getUserId(), "datasource", ds.getDatasourceId()));
        return m;
    }

    private Map<String, Object> toDatasetView(DashboardDataset ds, String userId) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", ds.getDatasetId());
        m.put("name", ds.getDatasetName());
        m.put("categoryName", ds.getCategoryName());
        m.put("data", parseJson(ds.getDataJson(), Map.of()));
        m.put("edit", permissionService.canEdit(userId, ds.getUserId(), "dataset", ds.getDatasetId()));
        m.put("delete", permissionService.canDelete(userId, ds.getUserId(), "dataset", ds.getDatasetId()));
        return m;
    }

    private Map<String, Object> toWidgetView(DashboardWidget w, String userId) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", w.getWidgetId());
        m.put("name", w.getWidgetName());
        m.put("categoryName", w.getCategoryName());
        m.put("data", parseJson(w.getDataJson(), Map.of()));
        m.put("edit", permissionService.canEdit(userId, w.getUserId(), "widget", w.getWidgetId()));
        m.put("delete", permissionService.canDelete(userId, w.getUserId(), "widget", w.getWidgetId()));
        return m;
    }

    private Object parseJson(String json, Object fallback) {
        if (json == null || json.isBlank()) {
            return fallback;
        }
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (Exception e) {
            return fallback;
        }
    }
}
