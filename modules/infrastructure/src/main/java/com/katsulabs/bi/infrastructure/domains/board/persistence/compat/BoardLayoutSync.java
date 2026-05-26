package com.katsulabs.bi.infrastructure.domains.board.persistence.compat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.katsulabs.bi.infrastructure.domains.board.persistence.mybatis.BoardWidgetMapper;
import com.katsulabs.bi.infrastructure.domains.board.persistence.mybatis.BoardWidgetRow;

/** layout_json ↔ ib_board_widget 동기화 */
public final class BoardLayoutSync {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int FREE_GRID = 47;

    private BoardLayoutSync() {}

    public static void syncFromLayoutJson(BoardWidgetMapper mapper, long boardId, String layoutJson) {
        mapper.deleteByBoardId(boardId);
        if (layoutJson == null || layoutJson.isBlank()) {
            return;
        }
        int sort = 0;
        try {
            JsonNode root = MAPPER.readTree(layoutJson);
            String type = root.path("type").asText("grid");
            if ("free".equals(type)) {
                for (JsonNode w : root.path("widgets")) {
                    long widgetId = w.path("widgetId").asLong();
                    if (widgetId <= 0) continue;
                    insertFree(mapper, boardId, widgetId, w, sort++);
                }
                return;
            }
            int rowIndex = 0;
            for (JsonNode row : root.path("rows")) {
                String rowType = row.path("type").asText("widget");
                if ("param".equals(rowType)) {
                    rowIndex++;
                    continue;
                }
                if (!"widget".equals(rowType)) {
                    rowIndex++;
                    continue;
                }
                Integer heightPx = parseHeightPx(row.path("height").asText(null));
                int col = 0;
                for (JsonNode slot : row.path("widgets")) {
                    long widgetId = slot.path("widgetId").asLong();
                    if (widgetId <= 0) continue;
                    int width = slot.path("width").asInt(12);
                    insertGrid(mapper, boardId, widgetId, rowIndex, col++, width, heightPx, sort++);
                }
                rowIndex++;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("layout_json 파싱 실패", e);
        }
    }

    /** layout_json 에 위젯 슬롯이 없고 ib_board_widget 만 있을 때 복원 */
    public static String resolveLayoutJson(String layoutJson, List<BoardWidgetRow> placements) {
        if (placements == null || placements.isEmpty()) {
            return layoutJson;
        }
        if (countWidgetSlots(layoutJson) > 0) {
            return layoutJson;
        }
        return composeFromPlacements(layoutJson, placements);
    }

    private static int countWidgetSlots(String layoutJson) {
        if (layoutJson == null || layoutJson.isBlank()) {
            return 0;
        }
        try {
            JsonNode root = MAPPER.readTree(layoutJson);
            if ("free".equals(root.path("type").asText())) {
                return root.path("widgets").size();
            }
            int n = 0;
            for (JsonNode row : root.path("rows")) {
                if ("widget".equals(row.path("type").asText())) {
                    n += row.path("widgets").size();
                }
            }
            return n;
        } catch (Exception e) {
            return 0;
        }
    }

    private static String composeFromPlacements(String existingJson, List<BoardWidgetRow> placements) {
        try {
            ObjectNode root = existingJson != null && !existingJson.isBlank()
                    ? (ObjectNode) MAPPER.readTree(existingJson)
                    : MAPPER.createObjectNode();
            if ("free".equals(root.path("type").asText(null))) {
                return composeFree(root, placements);
            }
            ArrayNode rows = MAPPER.createArrayNode();
            for (JsonNode row : root.path("rows")) {
                if ("param".equals(row.path("type").asText())) {
                    rows.add(row.deepCopy());
                }
            }
            rows.addAll(buildGridRows(placements));
            root.put("type", root.has("type") ? root.get("type").asText() : "grid");
            root.set("rows", rows);
            return MAPPER.writeValueAsString(root);
        } catch (Exception e) {
            throw new IllegalStateException("layout_json 조립 실패", e);
        }
    }

    private static String composeFree(ObjectNode root, List<BoardWidgetRow> placements) throws Exception {
        ArrayNode widgets = root.putArray("widgets");
        for (BoardWidgetRow p : placements) {
            ObjectNode w = widgets.addObject();
            w.put("widgetId", p.getWidgetId());
            w.put("name", p.getWidgetName() != null ? p.getWidgetName() : "widget");
            w.put("x", p.getPosX() != null ? p.getPosX() : 0);
            w.put("y", p.getPosY() != null ? p.getPosY() : 0);
            w.put("ex", p.getPosEx() != null ? p.getPosEx() : 4);
            w.put("ey", p.getPosEy() != null ? p.getPosEy() : 4);
        }
        return MAPPER.writeValueAsString(root);
    }

    private static ArrayNode buildGridRows(List<BoardWidgetRow> placements) {
        Map<Integer, List<BoardWidgetRow>> byRow = new TreeMap<>();
        for (BoardWidgetRow p : placements) {
            byRow.computeIfAbsent(p.getRowIndex(), k -> new ArrayList<>()).add(p);
        }
        ArrayNode rows = MAPPER.createArrayNode();
        for (var entry : byRow.entrySet()) {
            ObjectNode row = rows.addObject();
            row.put("type", "widget");
            BoardWidgetRow first = entry.getValue().get(0);
            if (first.getHeightPx() != null) {
                row.put("height", String.valueOf(first.getHeightPx()));
            } else {
                row.put("height", "320");
            }
            ArrayNode slots = row.putArray("widgets");
            entry.getValue().stream()
                    .sorted((a, b) -> Integer.compare(a.getColumnIndex(), b.getColumnIndex()))
                    .forEach(p -> {
                        ObjectNode slot = slots.addObject();
                        slot.put("widgetId", p.getWidgetId());
                        slot.put("name", p.getWidgetName() != null ? p.getWidgetName() : "widget");
                        slot.put("width", p.getWidthCols());
                    });
        }
        return rows;
    }

    private static void insertGrid(
            BoardWidgetMapper mapper,
            long boardId,
            long widgetId,
            int rowIndex,
            int columnIndex,
            int widthCols,
            Integer heightPx,
            int sortOrder) {
        BoardWidgetRow row = new BoardWidgetRow();
        row.setBoardId(boardId);
        row.setWidgetId(widgetId);
        row.setRowIndex(rowIndex);
        row.setColumnIndex(columnIndex);
        row.setWidthCols(widthCols);
        row.setHeightPx(heightPx);
        row.setSortOrder(sortOrder);
        mapper.insert(row);
    }

    private static void insertFree(
            BoardWidgetMapper mapper, long boardId, long widgetId, JsonNode w, int sortOrder) {
        BoardWidgetRow row = new BoardWidgetRow();
        row.setBoardId(boardId);
        row.setWidgetId(widgetId);
        row.setRowIndex(0);
        row.setColumnIndex(0);
        row.setWidthCols(FREE_GRID);
        row.setPosX(w.path("x").asInt(0));
        row.setPosY(w.path("y").asInt(0));
        row.setPosEx(w.path("ex").asInt(4));
        row.setPosEy(w.path("ey").asInt(4));
        row.setSortOrder(sortOrder);
        mapper.insert(row);
    }

    private static Integer parseHeightPx(String height) {
        if (height == null || height.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(height.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
