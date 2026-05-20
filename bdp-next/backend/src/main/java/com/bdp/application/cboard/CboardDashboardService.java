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
