package com.katsulabs.bi.infrastructure.domains.widget.persistence.compat;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.katsulabs.bi.infrastructure.domains.widget.persistence.mybatis.WidgetBindingMapper;
import com.katsulabs.bi.infrastructure.domains.widget.persistence.mybatis.WidgetBindingRow;
import com.katsulabs.bi.infrastructure.domains.widget.persistence.mybatis.WidgetMapper;
import com.katsulabs.bi.infrastructure.domains.widget.persistence.mybatis.WidgetRow;

/** CBoard 호환 widget data_json 조립·분해 (v2 정규화 테이블) */
public final class CboardWidgetJson {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private CboardWidgetJson() {}

    public static String compose(WidgetRow row, List<WidgetBindingRow> bindings) {
        try {
            ObjectNode root = MAPPER.createObjectNode();
            root.put("datasetId", row.getDatasetId());
            root.put("datasource", row.getDatasourceId());
            root.putArray("expressions");
            root.putArray("filterGroups");

            ObjectNode config = root.putObject("config");
            config.put("chart_type", row.getChartType());
            config.putArray("groups");

            ArrayNode keys = config.putArray("keys");
            ArrayNode values = config.putArray("values");
            ObjectNode valueGroup = MAPPER.createObjectNode();
            ArrayNode cols = valueGroup.putArray("cols");
            values.add(valueGroup);

            for (WidgetBindingRow b : bindings) {
                String axis = b.getAxis() == null ? "" : b.getAxis().toUpperCase();
                if ("VALUE".equals(axis)) {
                    ObjectNode col = cols.addObject();
                    col.put("type", "column");
                    col.put("col", b.getColumnName());
                    if (b.getAlias() != null && !b.getAlias().isBlank()) {
                        col.put("alias", b.getAlias());
                    }
                    if (b.getAggregateFn() != null && !b.getAggregateFn().isBlank()) {
                        col.put("aggregate_type", b.getAggregateFn());
                    }
                } else {
                    ObjectNode key = keys.addObject();
                    key.put("type", "column");
                    key.put("col", b.getColumnName());
                    if (b.getAlias() != null && !b.getAlias().isBlank()) {
                        key.put("alias", b.getAlias());
                    }
                }
            }
            if (row.getOptionsJson() != null && !row.getOptionsJson().isBlank()) {
                var opts = MAPPER.readTree(row.getOptionsJson());
                if (opts.isObject()) {
                    opts.fields().forEachRemaining(e -> config.set(e.getKey(), e.getValue()));
                }
            }
            return MAPPER.writeValueAsString(root);
        } catch (Exception e) {
            throw new IllegalStateException("widget data_json 조립 실패", e);
        }
    }

    public static void persistFromJson(
            WidgetMapper widgetMapper,
            WidgetBindingMapper bindingMapper,
            WidgetRow row,
            String dataJson) {
        try {
            var root = MAPPER.readTree(dataJson);
            row.setDatasetId(root.path("datasetId").asLong());
            if (row.getDatasetId() == 0) {
                row.setDatasetId(root.path("dataset").asLong());
            }
            var config = root.path("config");
            row.setChartType(config.path("chart_type").asText("table"));
            row.setDatasourceId(root.path("datasource").asLong());

            if (row.getId() == null) {
                widgetMapper.insert(row);
            } else {
                widgetMapper.update(row);
                bindingMapper.deleteByWidgetId(row.getId());
            }
            long widgetId = row.getId();
            int order = 0;
            for (var key : config.path("keys")) {
                insertBinding(bindingMapper, widgetId, "ROW", key, order++);
            }
            for (var group : config.path("groups")) {
                insertBinding(bindingMapper, widgetId, "COLUMN", group, order++);
            }
            for (var vg : config.path("values")) {
                for (var col : vg.path("cols")) {
                    insertBinding(bindingMapper, widgetId, "VALUE", col, order++);
                }
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("widget data_json 파싱 실패", e);
        }
    }

    private static void insertBinding(
            WidgetBindingMapper bindingMapper,
            long widgetId,
            String axis,
            com.fasterxml.jackson.databind.JsonNode node,
            int sortOrder) {
        String col = node.path("col").asText();
        if (col.isBlank()) {
            return;
        }
        WidgetBindingRow b = new WidgetBindingRow();
        b.setWidgetId(widgetId);
        b.setAxis(axis);
        b.setColumnName(col);
        String alias = node.path("alias").asText(null);
        if (alias != null && !alias.isBlank()) {
            b.setAlias(alias);
        }
        String agg = node.path("aggregate_type").asText(null);
        if (agg != null && !agg.isBlank()) {
            b.setAggregateFn(agg);
        }
        b.setSortOrder(sortOrder);
        bindingMapper.insert(b);
    }
}
