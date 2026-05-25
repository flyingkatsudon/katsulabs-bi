package org.cboard.infrastructure.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.cboard.application.dataset.DatasetPreviewResult;
import org.cboard.application.dataset.DatasetSqlPreviewPort;
import org.cboard.application.support.JsonMapper;
import org.cboard.domain.dataset.DatasetRepository;
import org.cboard.domain.datasource.DatasourceRepository;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class DatasetSqlPreviewAdapter implements DatasetSqlPreviewPort {

    private static final TypeReference<Map<String, String>> MAP_TYPE = new TypeReference<>() {};

    private final DatasetRepository datasetRepository;
    private final DatasourceRepository datasourceRepository;

    public DatasetSqlPreviewAdapter(
            DatasetRepository datasetRepository, DatasourceRepository datasourceRepository) {
        this.datasetRepository = datasetRepository;
        this.datasourceRepository = datasourceRepository;
    }

    @Override
    public DatasetPreviewResult preview(long datasetId, int maxRows) {
        var dataset = datasetRepository
                .findById(datasetId)
                .orElseThrow(() -> new IllegalArgumentException("데이터셋을 찾을 수 없습니다: " + datasetId));

        JsonNode root;
        try {
            root = JsonMapper.mapper().readTree(dataset.dataJson());
        } catch (Exception e) {
            throw new IllegalArgumentException("data_json 파싱 실패", e);
        }
        long datasourceId = root.path("datasource").asLong();
        if (datasourceId == 0) {
            throw new IllegalArgumentException("data_json 에 datasource 가 없습니다.");
        }
        String sql = root.path("query").path("sql").asText(null);
        if (sql == null || sql.isBlank()) {
            throw new IllegalArgumentException("data_json 에 query.sql 이 없습니다.");
        }
        return previewByDatasource(datasourceId, sql, maxRows);
    }

    @Override
    public DatasetPreviewResult previewByDatasource(long datasourceId, String sql, int maxRows) {
        if (sql == null || sql.isBlank()) {
            throw new IllegalArgumentException("SQL 이 비어 있습니다.");
        }
        var datasource = datasourceRepository
                .findById(datasourceId)
                .orElseThrow(() -> new IllegalArgumentException("데이터소스를 찾을 수 없습니다: " + datasourceId));
        if (!"jdbc".equalsIgnoreCase(datasource.type())) {
            throw new IllegalArgumentException("JDBC 데이터소스만 미리보기를 지원합니다.");
        }

        Map<String, String> config = JsonMapper.fromJson(datasource.configJson(), MAP_TYPE);
        String driver = config.get("driver");
        String url = config.get("jdbcurl");
        String username = config.getOrDefault("username", "");
        String password = config.getOrDefault("password", "");

        return executeQuery(driver, url, username, password, wrapWithLimit(sql, maxRows));
    }

    private static DatasetPreviewResult executeQuery(
            String driver, String url, String username, String password, String sql) {
        try {
            Class.forName(driver);
            try (Connection connection = DriverManager.getConnection(url, username, password);
                    Statement statement = connection.createStatement();
                    ResultSet rs = statement.executeQuery(sql)) {
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();
                List<String> columns = new ArrayList<>(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    columns.add(meta.getColumnLabel(i));
                }
                List<Map<String, Object>> rows = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(columns.get(i - 1), rs.getObject(i));
                    }
                    rows.add(row);
                }
                return new DatasetPreviewResult(columns, rows);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("SQL 미리보기 실패: " + e.getMessage(), e);
        }
    }

    private static String wrapWithLimit(String sql, int maxRows) {
        String trimmed = sql.trim();
        if (trimmed.toUpperCase().contains(" LIMIT ")) {
            return trimmed;
        }
        return trimmed + " LIMIT " + maxRows;
    }
}
