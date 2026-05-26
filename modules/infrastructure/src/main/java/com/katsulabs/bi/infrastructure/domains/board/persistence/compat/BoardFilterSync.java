package com.katsulabs.bi.infrastructure.domains.board.persistence.compat;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.katsulabs.bi.infrastructure.domains.board.persistence.mybatis.BoardFilterMapper;
import com.katsulabs.bi.infrastructure.domains.board.persistence.mybatis.BoardFilterRow;

/** layout_json param rows ↔ ib_board_filter / ib_board_filter_option 동기화 */
public final class BoardFilterSync {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private BoardFilterSync() {}

    public static void syncFromLayoutJson(BoardFilterMapper mapper, long boardId, String layoutJson) {
        mapper.deleteOptionsByBoardId(boardId);
        mapper.deleteByBoardId(boardId);
        if (layoutJson == null || layoutJson.isBlank()) {
            return;
        }
        try {
            JsonNode root = MAPPER.readTree(layoutJson);
            int sort = 0;
            for (JsonNode row : root.path("rows")) {
                if (!"param".equals(row.path("type").asText())) {
                    continue;
                }
                for (JsonNode param : row.path("params")) {
                    String col = param.path("col").asText("").trim();
                    if (col.isEmpty()) {
                        continue;
                    }
                    BoardFilterRow filter = new BoardFilterRow();
                    filter.setBoardId(boardId);
                    filter.setColumnName(col);
                    String label = param.path("label").asText(null);
                    filter.setLabel(label != null && !label.isBlank() ? label : col);
                    String defaultValue = param.path("defaultValue").asText(null);
                    if (defaultValue != null && !defaultValue.isBlank()) {
                        filter.setDefaultValue(defaultValue);
                    }
                    filter.setSortOrder(sort++);
                    mapper.insert(filter);
                    for (JsonNode valueNode : param.path("values")) {
                        String option = valueNode.asText("").trim();
                        if (!option.isEmpty()) {
                            mapper.insertOption(filter.getFilterId(), option);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("layout_json param 파싱 실패", e);
        }
    }

    public static String resolveLayoutJson(String layoutJson, List<BoardFilterRow> filters) {
        if (filters == null || filters.isEmpty()) {
            return layoutJson;
        }
        if (countParamRows(layoutJson) > 0) {
            return layoutJson;
        }
        return injectParamRows(layoutJson, filters);
    }

    private static int countParamRows(String layoutJson) {
        if (layoutJson == null || layoutJson.isBlank()) {
            return 0;
        }
        try {
            JsonNode root = MAPPER.readTree(layoutJson);
            int n = 0;
            for (JsonNode row : root.path("rows")) {
                if ("param".equals(row.path("type").asText())) {
                    n++;
                }
            }
            return n;
        } catch (Exception e) {
            return 0;
        }
    }

    private static String injectParamRows(String layoutJson, List<BoardFilterRow> filters) {
        try {
            ObjectNode root = layoutJson != null && !layoutJson.isBlank()
                    ? (ObjectNode) MAPPER.readTree(layoutJson)
                    : MAPPER.createObjectNode();
            if (!root.has("type")) {
                root.put("type", "grid");
            }
            ArrayNode rows = MAPPER.createArrayNode();
            ObjectNode paramRow = rows.addObject();
            paramRow.put("type", "param");
            ArrayNode params = paramRow.putArray("params");
            for (BoardFilterRow filter : filters) {
                ObjectNode param = params.addObject();
                param.put("col", filter.getColumnName());
                if (filter.getLabel() != null && !filter.getLabel().isBlank()) {
                    param.put("label", filter.getLabel());
                }
                if (filter.getDefaultValue() != null && !filter.getDefaultValue().isBlank()) {
                    param.put("defaultValue", filter.getDefaultValue());
                }
                ArrayNode values = param.putArray("values");
                List<String> options =
                        filter.getOptionValues() != null ? filter.getOptionValues() : List.of();
                for (String option : options) {
                    values.add(option);
                }
            }
            for (JsonNode row : root.path("rows")) {
                if (!"param".equals(row.path("type").asText())) {
                    rows.add(row.deepCopy());
                }
            }
            root.set("rows", rows);
            return MAPPER.writeValueAsString(root);
        } catch (Exception e) {
            throw new IllegalStateException("layout_json param 조립 실패", e);
        }
    }

    public static List<BoardFilterRow> loadFilters(BoardFilterMapper mapper, long boardId) {
        List<BoardFilterRow> filters = mapper.findByBoardId(boardId);
        for (BoardFilterRow filter : filters) {
            filter.setOptionValues(new ArrayList<>(mapper.findOptionValuesByFilterId(filter.getFilterId())));
        }
        return filters;
    }
}
