package com.bdp.application.cboard;

import com.bdp.api.cboard.ServiceStatusDto;
import com.bdp.domain.metadata.DashboardBoard;
import com.bdp.domain.metadata.DashboardCategory;
import com.bdp.domain.metadata.DashboardDataset;
import com.bdp.domain.metadata.DashboardDatasource;
import com.bdp.domain.metadata.DashboardWidget;
import com.bdp.infrastructure.persistence.DashboardBoardRepository;
import com.bdp.infrastructure.persistence.DashboardCategoryRepository;
import com.bdp.infrastructure.persistence.DashboardDatasetRepository;
import com.bdp.infrastructure.persistence.DashboardDatasourceRepository;
import com.bdp.infrastructure.persistence.DashboardWidgetRepository;
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
    private final ObjectMapper objectMapper;

    public CboardDashboardService(
            DashboardBoardRepository boardRepository,
            DashboardCategoryRepository categoryRepository,
            DashboardWidgetRepository widgetRepository,
            DashboardDatasetRepository datasetRepository,
            DashboardDatasourceRepository datasourceRepository,
            ObjectMapper objectMapper) {
        this.boardRepository = boardRepository;
        this.categoryRepository = categoryRepository;
        this.widgetRepository = widgetRepository;
        this.datasetRepository = datasetRepository;
        this.datasourceRepository = datasourceRepository;
        this.objectMapper = objectMapper;
    }

    public List<Map<String, Object>> getBoardList(String userId) {
        return boardRepository.findAll().stream().map(b -> toBoardView(b, userId)).toList();
    }

    public Map<String, Object> getBoardData(Long id, String userId) {
        DashboardBoard board = boardRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found"));
        return toBoardView(board, userId);
    }

    public List<DashboardCategory> getCategoryList() {
        return categoryRepository.findAllByOrderByCategoryNameAsc();
    }

    public List<Map<String, Object>> getDatasourceList(String userId) {
        return datasourceRepository.findByUserIdOrderBySourceNameAsc(userId).stream()
                .map(this::toDatasourceView)
                .toList();
    }

    public List<Map<String, Object>> getDatasetList(String userId) {
        return datasetRepository.findByUserIdOrderByDatasetNameAsc(userId).stream()
                .map(this::toDatasetView)
                .toList();
    }

    public List<Map<String, Object>> getAllDatasetList() {
        return datasetRepository.findAllByOrderByDatasetNameAsc().stream()
                .map(this::toDatasetView)
                .toList();
    }

    public List<Map<String, Object>> getWidgetList(String userId) {
        return widgetRepository.findByUserIdOrderByWidgetNameAsc(userId).stream()
                .map(this::toWidgetView)
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

    public List<Map<String, Object>> getConfigParams(String type, String page) {
        return List.of(
                Map.of("name", "jdbcurl", "label", "JDBC URL", "type", "input"),
                Map.of("name", "driver", "label", "Driver", "type", "input"),
                Map.of("name", "username", "label", "Username", "type", "input"),
                Map.of("name", "password", "label", "Password", "type", "password"));
    }

    public String getConfigView(String type, String page) {
        return "<div class=\"form-group\"><label>JDBC URL</label><input class=\"form-control\" name=\"jdbcurl\"/></div>";
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
        m.put("edit", userId.equals(board.getUserId()));
        m.put("delete", userId.equals(board.getUserId()));
        if (board.getCategoryId() != null) {
            categoryRepository.findById(board.getCategoryId()).ifPresent(c -> m.put("categoryName", c.getCategoryName()));
        }
        m.put("layout", parseJson(board.getLayoutJson(), defaultLayout()));
        return m;
    }

    private Map<String, Object> defaultLayout() {
        Map<String, Object> layout = new LinkedHashMap<>();
        layout.put("type", "grid");
        layout.put("rows", new ArrayList<>());
        return layout;
    }

    private Map<String, Object> toDatasourceView(DashboardDatasource ds) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", ds.getDatasourceId());
        m.put("name", ds.getSourceName());
        m.put("type", ds.getSourceType());
        m.put("config", parseJson(ds.getConfig(), Map.of()));
        m.put("edit", true);
        m.put("delete", true);
        return m;
    }

    private Map<String, Object> toDatasetView(DashboardDataset ds) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", ds.getDatasetId());
        m.put("name", ds.getDatasetName());
        m.put("categoryName", ds.getCategoryName());
        m.put("data", parseJson(ds.getDataJson(), Map.of()));
        m.put("edit", true);
        m.put("delete", true);
        return m;
    }

    private Map<String, Object> toWidgetView(DashboardWidget w) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", w.getWidgetId());
        m.put("name", w.getWidgetName());
        m.put("categoryName", w.getCategoryName());
        m.put("data", parseJson(w.getDataJson(), Map.of()));
        m.put("edit", true);
        m.put("delete", true);
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
