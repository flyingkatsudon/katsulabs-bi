package com.katsulabs.bi.infrastructure.domains.dataset.persistence.compat;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.katsulabs.bi.infrastructure.domains.dataset.persistence.mybatis.DatasetColumnMapper;
import com.katsulabs.bi.infrastructure.domains.dataset.persistence.mybatis.DatasetColumnRow;
import com.katsulabs.bi.infrastructure.domains.dataset.persistence.mybatis.DatasetMapper;
import com.katsulabs.bi.infrastructure.domains.dataset.persistence.mybatis.DatasetRow;

/** 레거시 v1 dataset data_json 조립·분해 (v2 정규화 테이블) */
public final class LegacyDatasetDataJson {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private LegacyDatasetDataJson() {}

    public static String compose(DatasetRow row, List<DatasetColumnRow> columns) {
        try {
            ObjectNode root = MAPPER.createObjectNode();
            root.put("datasource", row.getDatasourceId());
            ObjectNode query = root.putObject("query");
            query.put("sql", row.getSqlText());
            root.putArray("filters");
            root.putArray("expressions");

            ArrayNode selects = root.putArray("selects");
            ObjectNode schema = root.putObject("schema");
            ArrayNode dimensions = schema.putArray("dimension");
            ArrayNode measures = schema.putArray("measure");
            int di = 1;
            int mi = 1;
            for (DatasetColumnRow col : columns) {
                selects.add(col.getColumnName());
                ObjectNode entry = MAPPER.createObjectNode();
                entry.put("column", col.getColumnName());
                entry.put("type", "column");
                if ("MEASURE".equalsIgnoreCase(col.getKind())) {
                    entry.put("id", "m" + mi++);
                    measures.add(entry);
                } else {
                    entry.put("id", "d" + di++);
                    if (col.getAlias() != null && !col.getAlias().isBlank()) {
                        entry.put("alias", col.getAlias());
                    }
                    dimensions.add(entry);
                }
            }
            return MAPPER.writeValueAsString(root);
        } catch (Exception e) {
            throw new IllegalStateException("dataset data_json 조립 실패", e);
        }
    }

    public static void persistFromJson(
            DatasetMapper datasetMapper,
            DatasetColumnMapper columnMapper,
            DatasetRow row,
            String dataJson) {
        try {
            var root = MAPPER.readTree(dataJson);
            row.setDatasourceId(root.path("datasource").asLong());
            row.setSqlText(root.path("query").path("sql").asText(""));
            if (row.getDatasourceId() == 0 || row.getSqlText() == null || row.getSqlText().isBlank()) {
                throw new IllegalArgumentException("dataset data_json 에 datasource·query.sql 이 필요합니다.");
            }
            if (row.getId() == null) {
                datasetMapper.insert(row);
            } else {
                datasetMapper.update(row);
                columnMapper.deleteByDatasetId(row.getId());
            }
            long datasetId = row.getId();
            Set<String> seen = new LinkedHashSet<>();
            int order = 0;
            var schema = root.path("schema");
            for (var node : schema.path("dimension")) {
                String column = node.path("column").asText();
                if (column.isBlank() || !seen.add(column)) continue;
                insertColumn(columnMapper, datasetId, column, "DIMENSION", node.path("alias").asText(null), order++);
            }
            for (var node : schema.path("measure")) {
                String column = node.path("column").asText();
                if (column.isBlank() || !seen.add(column)) continue;
                insertColumn(columnMapper, datasetId, column, "MEASURE", node.path("alias").asText(null), order++);
            }
            for (var sel : root.path("selects")) {
                String column = sel.asText();
                if (column == null || column.isBlank() || !seen.add(column)) continue;
                insertColumn(columnMapper, datasetId, column, "DIMENSION", null, order++);
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("dataset data_json 파싱 실패", e);
        }
    }

    private static void insertColumn(
            DatasetColumnMapper columnMapper,
            long datasetId,
            String columnName,
            String kind,
            String alias,
            int sortOrder) {
        DatasetColumnRow col = new DatasetColumnRow();
        col.setDatasetId(datasetId);
        col.setColumnName(columnName);
        col.setKind(kind);
        col.setAlias(alias);
        col.setSortOrder(sortOrder);
        columnMapper.insert(col);
    }
}
