package com.insightboard.api.application.cboard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CboardExportService {

    private final CboardDashboardService dashboardService;
    private final ObjectMapper objectMapper;

    public CboardExportService(CboardDashboardService dashboardService, ObjectMapper objectMapper) {
        this.dashboardService = dashboardService;
        this.objectMapper = objectMapper;
    }

    /** 레거시 exportBoard — 간이 TSV/XLS 호환 바이트 (탭 구분). */
    public byte[] exportBoard(Long boardId, String userId) {
        Map<String, Object> board = dashboardService.getBoardData(boardId, userId);
        StringBuilder sb = new StringBuilder();
        sb.append("Board: ").append(board.get("name")).append("\n");
        Object layout = board.get("layout");
        if (layout instanceof Map<?, ?> layoutMap) {
            Object rows = layoutMap.get("rows");
            if (rows instanceof List<?> rowList) {
                for (Object rowObj : rowList) {
                    if (rowObj instanceof Map<?, ?> row) {
                        Object widgets = row.get("widgets");
                        if (widgets instanceof List<?> widgetList) {
                            for (Object wObj : widgetList) {
                                if (wObj instanceof Map<?, ?> cell) {
                                    sb.append(cell.get("name")).append("\t");
                                }
                            }
                            sb.append("\n");
                        }
                    }
                }
            }
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] tableToXls(String dataJson) {
        try {
            Map<String, Object> data = objectMapper.readValue(dataJson, new TypeReference<>() {});
            @SuppressWarnings("unchecked")
            List<List<Object>> rows = (List<List<Object>>) data.getOrDefault("data", List.of());
            StringBuilder sb = new StringBuilder();
            for (List<Object> row : rows) {
                List<String> cells = new ArrayList<>();
                for (Object c : row) {
                    cells.add(c != null ? String.valueOf(c) : "");
                }
                sb.append(String.join("\t", cells)).append("\n");
            }
            return sb.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            return new byte[0];
        }
    }
}
