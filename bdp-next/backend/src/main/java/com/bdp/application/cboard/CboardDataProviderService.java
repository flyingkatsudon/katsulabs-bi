package com.bdp.application.cboard;

import com.bdp.api.cboard.ServiceStatusDto;
import com.bdp.application.cboard.config.AggConfigDto;
import com.bdp.application.cboard.config.ViewAggConfigDto;
import com.bdp.application.cboard.dto.AggregateResultDto;
import com.bdp.application.cboard.dto.ColumnIndexDto;
import com.bdp.application.cboard.dto.DataProviderResultDto;
import com.bdp.domain.metadata.DashboardDataset;
import com.bdp.domain.metadata.DashboardDatasource;
import com.bdp.infrastructure.persistence.DashboardDatasetRepository;
import com.bdp.infrastructure.persistence.DashboardDatasourceRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.stereotype.Service;

@Service
public class CboardDataProviderService {

    private static final String DEFAULT_TABLE = "daily_kwd_trend_cnt_minimal_v2";

    private final DashboardDatasetRepository datasetRepository;
    private final DashboardDatasourceRepository datasourceRepository;
    private final DataSource dataSource;
    private final ObjectMapper objectMapper;
    private final JdbcAggregateQueryBuilder queryBuilder;

    public CboardDataProviderService(
            DashboardDatasetRepository datasetRepository,
            DashboardDatasourceRepository datasourceRepository,
            DataSource dataSource,
            ObjectMapper objectMapper,
            JdbcAggregateQueryBuilder queryBuilder) {
        this.datasetRepository = datasetRepository;
        this.datasourceRepository = datasourceRepository;
        this.dataSource = dataSource;
        this.objectMapper = objectMapper;
        this.queryBuilder = queryBuilder;
    }

    public AggregateResultDto queryAggData(
            Long datasourceId, Long datasetId, String cfg, Map<String, String> queryParams) {
        DatasetQueryContext ctx = resolveContext(datasourceId, datasetId, queryParams);
        AggConfigDto agg = parseAggConfig(cfg);
        JdbcAggregateQueryBuilder.BuiltQuery built = queryBuilder.build(ctx.fromClause(), agg);
        return executeAggregate(built);
    }

    public String viewAggDataQuery(
            Long datasourceId, Long datasetId, String cfg, Map<String, String> queryParams) {
        DatasetQueryContext ctx = resolveContext(datasourceId, datasetId, queryParams);
        AggConfigDto agg = parseAggConfig(cfg);
        return queryBuilder.build(ctx.fromClause(), agg).sql();
    }

    public DataProviderResultDto getColumns(
            Long datasourceId, Long datasetId, Map<String, String> queryParams) {
        DatasetQueryContext ctx = resolveContext(datasourceId, datasetId, queryParams);
        String sql = queryBuilder.buildPreviewColumnsQuery(ctx.fromClause());
        try (Connection conn = dataSource.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            String[] columns = new String[colCount];
            for (int i = 1; i <= colCount; i++) {
                columns[i - 1] = meta.getColumnLabel(i);
            }
            List<String[]> rows = new ArrayList<>();
            int n = 0;
            while (rs.next() && n < 100) {
                String[] row = new String[colCount];
                for (int i = 1; i <= colCount; i++) {
                    Object v = rs.getObject(i);
                    row[i - 1] = v != null ? String.valueOf(v) : "";
                }
                rows.add(row);
                n++;
            }
            return new DataProviderResultDto(
                    rows.toArray(String[][]::new), columns, "1", rows.size());
        } catch (Exception e) {
            return new DataProviderResultDto(new String[0][], new String[0], e.getMessage(), 0);
        }
    }

    public String[] getDimensionValues(
            Long datasourceId,
            Long datasetId,
            String columnName,
            String cfg,
            Map<String, String> queryParams) {
        DatasetQueryContext ctx = resolveContext(datasourceId, datasetId, queryParams);
        String sql = queryBuilder.buildDistinctQuery(ctx.fromClause(), columnName);
        Set<String> values = new LinkedHashSet<>();
        try (Connection conn = dataSource.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Object v = rs.getObject(1);
                if (v != null) {
                    values.add(String.valueOf(v));
                }
            }
        } catch (Exception e) {
            return new String[0];
        }
        return values.toArray(String[]::new);
    }

    public ServiceStatusDto testDatasource(String datasourceJson, String queryJson) {
        try {
            Map<String, Object> ds = objectMapper.readValue(datasourceJson, new TypeReference<>() {});
            @SuppressWarnings("unchecked")
            Map<String, Object> config = ds.get("config") instanceof Map<?, ?> m
                    ? (Map<String, Object>) m
                    : ds;
            String url = str(config.get("jdbcurl"));
            if (url == null || url.isBlank()) {
                try (Connection c = dataSource.getConnection()) {
                    c.isValid(2);
                }
                return ServiceStatusDto.ok();
            }
            String driver = str(config.get("driver"));
            if (driver != null && !driver.isBlank()) {
                Class.forName(driver);
            }
            try (Connection c = DriverManager.getConnection(
                    url, str(config.get("username")), str(config.get("password")))) {
                c.isValid(2);
            }
            return ServiceStatusDto.ok();
        } catch (Exception e) {
            return ServiceStatusDto.fail(e.getMessage());
        }
    }

    public List<Map<String, Object>> getDatasourceParams(String type) {
        return List.of(
                Map.of("name", "driver", "label", "Driver", "type", "input", "required", true),
                Map.of("name", "jdbcurl", "label", "JDBC URL", "type", "input", "required", true),
                Map.of("name", "username", "label", "Username", "type", "input"),
                Map.of("name", "password", "label", "Password", "type", "password"));
    }

    public String getDatasourceView(String type) {
        return """
                <div class="form-group"><label>Driver</label><input class="form-control" name="driver"/></div>
                <div class="form-group"><label>JDBC URL</label><input class="form-control" name="jdbcurl"/></div>
                <div class="form-group"><label>Username</label><input class="form-control" name="username"/></div>
                <div class="form-group"><label>Password</label><input class="form-control" name="password" type="password"/></div>
                """;
    }

public AggConfigDto parseAggConfig(String cfg) {
        if (cfg == null || cfg.isBlank() || "{}".equals(cfg.trim())) {
            return defaultAggConfig();
        }
        try {
            ViewAggConfigDto view = objectMapper.readValue(cfg, ViewAggConfigDto.class);
            return view.toAggConfig();
        } catch (Exception e) {
            return defaultAggConfig();
        }
    }

    private AggConfigDto defaultAggConfig() {
        AggConfigDto agg = new AggConfigDto();
        var dim = new com.bdp.application.cboard.config.DimensionConfigDto();
        dim.setColumnName("doc_date");
        agg.getRows().add(dim);
        var val = new com.bdp.application.cboard.config.ValueConfigDto();
        val.setColumn("doc_cnt_both");
        val.setAggType("sum");
        agg.getValues().add(val);
        return agg;
    }

    private DatasetQueryContext resolveContext(
            Long datasourceId, Long datasetId, Map<String, String> queryParams) {
        if (datasetId != null) {
            return datasetRepository
                    .findById(datasetId)
                    .map(this::contextFromDataset)
                    .orElse(new DatasetQueryContext(datasourceId, DEFAULT_TABLE));
        }
        if (queryParams != null && queryParams.get("sql") != null) {
            return new DatasetQueryContext(datasourceId, queryParams.get("sql"));
        }
        return new DatasetQueryContext(datasourceId, DEFAULT_TABLE);
    }

    @SuppressWarnings("unchecked")
    private DatasetQueryContext contextFromDataset(DashboardDataset dataset) {
        Long dsId = null;
        String from = DEFAULT_TABLE;
        try {
            Map<String, Object> data = objectMapper.readValue(dataset.getDataJson(), new TypeReference<>() {});
            if (data.get("datasource") instanceof Number n) {
                dsId = n.longValue();
            }
            Object query = data.get("query");
            if (query instanceof Map<?, ?> qm) {
                if (qm.get("sql") != null) {
                    from = String.valueOf(qm.get("sql"));
                } else if (qm.get("table") != null) {
                    from = String.valueOf(qm.get("table"));
                }
            }
        } catch (Exception ignored) {
            // use defaults
        }
        return new DatasetQueryContext(dsId, from);
    }

    private AggregateResultDto executeAggregate(JdbcAggregateQueryBuilder.BuiltQuery built) {
        List<ColumnIndexDto> columnList = new ArrayList<>();
        for (int i = 0; i < built.selectAliases().size(); i++) {
            String name = built.selectAliases().get(i);
            boolean isMeasure = built.groupByColumns().stream().noneMatch(g -> g.equals(name));
            columnList.add(new ColumnIndexDto(i, name, isMeasure ? "sum" : null));
        }
        List<String[]> dataRows = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(built.sql())) {
            int colCount = built.selectAliases().size();
            while (rs.next()) {
                String[] row = new String[colCount];
                for (int i = 1; i <= colCount; i++) {
                    Object v = rs.getObject(i);
                    row[i - 1] = v != null ? String.valueOf(v) : "";
                }
                dataRows.add(row);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Aggregate query failed: " + e.getMessage(), e);
        }
        return new AggregateResultDto(columnList, dataRows.toArray(String[][]::new));
    }

    private static String str(Object o) {
        return o == null ? null : String.valueOf(o);
    }
}
