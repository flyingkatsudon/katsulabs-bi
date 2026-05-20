package com.bdp.application.cboard;

import com.bdp.api.cboard.ServiceStatusDto;
import com.bdp.application.cboard.dto.AggregateResultDto;
import com.bdp.application.cboard.dto.ColumnIndexDto;
import com.bdp.application.cboard.dto.DataProviderResultDto;
import com.bdp.domain.analytics.DailyKwdTrend;
import com.bdp.domain.metadata.DashboardDatasource;
import com.bdp.infrastructure.persistence.DailyKwdTrendRepository;
import com.bdp.infrastructure.persistence.DashboardDatasourceRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.stereotype.Service;

/**
 * CBoard DataProvider SPI의 내재화 구현 (JPA/H2 기반).
 * 레거시 {@code getAggregateData}, {@code getColumns}, {@code getDimensionValues}, {@code test} 호환.
 */
@Service
public class CboardDataProviderService {

    private static final List<ColumnIndexDto> TREND_COLUMNS = List.of(
            new ColumnIndexDto(0, "doc_date", null),
            new ColumnIndexDto(1, "kwd_a", null),
            new ColumnIndexDto(2, "kwd_b", null),
            new ColumnIndexDto(3, "doc_cnt_both", "sum"));

    private final DailyKwdTrendRepository trendRepository;
    private final DashboardDatasourceRepository datasourceRepository;
    private final DataSource dataSource;
    private final ObjectMapper objectMapper;

    public CboardDataProviderService(
            DailyKwdTrendRepository trendRepository,
            DashboardDatasourceRepository datasourceRepository,
            DataSource dataSource,
            ObjectMapper objectMapper) {
        this.trendRepository = trendRepository;
        this.datasourceRepository = datasourceRepository;
        this.dataSource = dataSource;
        this.objectMapper = objectMapper;
    }

    public AggregateResultDto queryAggData(Long datasetId, String cfg) {
        List<DailyKwdTrend> rows = trendRepository.findAll();
        String[][] data = new String[rows.size()][4];
        for (int i = 0; i < rows.size(); i++) {
            DailyKwdTrend r = rows.get(i);
            data[i] = new String[] {
                r.getDocDate() != null ? r.getDocDate().toString() : "",
                nullToEmpty(r.getKwdA()),
                nullToEmpty(r.getKwdB()),
                r.getDocCntBoth() != null ? r.getDocCntBoth().toString() : "0"
            };
        }
        return new AggregateResultDto(TREND_COLUMNS, data);
    }

    public DataProviderResultDto getColumns(Long datasetId) {
        String[] columns = TREND_COLUMNS.stream().map(ColumnIndexDto::name).toArray(String[]::new);
        List<DailyKwdTrend> rows = trendRepository.findAll();
        String[][] data = new String[Math.min(rows.size(), 100)][columns.length];
        for (int i = 0; i < data.length; i++) {
            DailyKwdTrend r = rows.get(i);
            data[i] = new String[] {
                r.getDocDate() != null ? r.getDocDate().toString() : "",
                nullToEmpty(r.getKwdA()),
                nullToEmpty(r.getKwdB()),
                r.getDocCntBoth() != null ? r.getDocCntBoth().toString() : "0"
            };
        }
        return new DataProviderResultDto(data, columns, "ok", rows.size());
    }

    public String[] getDimensionValues(String columnName) {
        Set<String> values = new LinkedHashSet<>();
        for (DailyKwdTrend r : trendRepository.findAll()) {
            String v =
                    switch (columnName) {
                        case "doc_date" -> r.getDocDate() != null ? r.getDocDate().toString() : null;
                        case "kwd_a" -> r.getKwdA();
                        case "kwd_b" -> r.getKwdB();
                        case "doc_cnt_both" ->
                                r.getDocCntBoth() != null ? r.getDocCntBoth().toString() : null;
                        default -> null;
                    };
            if (v != null && !v.isBlank()) {
                values.add(v);
            }
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

    public Map<String, String> resolveDatasourceConfig(Long datasourceId) {
        if (datasourceId == null) {
            return Map.of();
        }
        return datasourceRepository
                .findById(datasourceId)
                .map(this::parseConfigMap)
                .orElse(Map.of());
    }

    private Map<String, String> parseConfigMap(DashboardDatasource ds) {
        try {
            Map<String, Object> raw = objectMapper.readValue(ds.getConfig(), new TypeReference<>() {});
            return raw.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));
        } catch (Exception e) {
            return Map.of();
        }
    }

    private static String str(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
