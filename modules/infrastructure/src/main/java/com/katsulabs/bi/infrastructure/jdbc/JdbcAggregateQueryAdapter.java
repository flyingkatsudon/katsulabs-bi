package com.katsulabs.bi.infrastructure.jdbc;

import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.katsulabs.bi.application.aggregate.AggregateColumnDto;
import com.katsulabs.bi.application.aggregate.AggregateQueryCommand;
import com.katsulabs.bi.application.aggregate.AggregateQueryPort;
import com.katsulabs.bi.application.aggregate.AggregateResultDto;
import com.katsulabs.bi.application.support.JsonMapper;
import com.katsulabs.bi.domain.dataset.DatasetRepository;
import com.katsulabs.bi.domain.datasource.DatasourceRepository;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

@Component
@RequiredArgsConstructor
public class JdbcAggregateQueryAdapter implements AggregateQueryPort {

    private static final TypeReference<Map<String, String>> MAP_TYPE = new TypeReference<>() {};
    private static final int MAX_ROWS = 500;

    private final DatasetRepository datasetRepository;
    private final DatasourceRepository datasourceRepository;


    @Override
    public AggregateResultDto query(AggregateQueryCommand command) {
        var ctx = buildContext(command);
        String sql = buildAggregateSql(ctx, false);
        return executeAggregateSql(ctx, sql);
    }

    @Override
    public String viewQuery(AggregateQueryCommand command) {
        var ctx = buildContext(command);
        return buildAggregateSql(ctx, false);
    }

    private QueryContext buildContext(AggregateQueryCommand command) {
        if (command.datasetId() == null) {
            throw new IllegalArgumentException("datasetId 가 필요합니다.");
        }
        var dataset = datasetRepository
                .findById(command.datasetId())
                .orElseThrow(() -> new IllegalArgumentException("데이터셋을 찾을 수 없습니다: " + command.datasetId()));

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
        String datasetSql = root.path("query").path("sql").asText(null);
        if (datasetSql == null || datasetSql.isBlank()) {
            throw new IllegalArgumentException("data_json 에 query.sql 이 없습니다.");
        }

        var datasource = datasourceRepository
                .findById(datasourceId)
                .orElseThrow(() -> new IllegalArgumentException("데이터소스를 찾을 수 없습니다: " + datasourceId));
        if (!"jdbc".equalsIgnoreCase(datasource.type())) {
            throw new IllegalArgumentException("JDBC 데이터소스만 집계를 지원합니다.");
        }

        Map<String, String> config = JsonMapper.fromJson(datasource.configJson(), MAP_TYPE);
        AggCfg cfg = parseCfg(command.configJson());

        return new QueryContext(
                config.get("driver"),
                config.get("jdbcurl"),
                config.getOrDefault("username", ""),
                config.getOrDefault("password", ""),
                datasetSql.trim(),
                cfg);
    }

    private AggregateResultDto executeAggregateSql(QueryContext ctx, String sql) {
        List<AggregateColumnDto> columnList = buildColumnList(ctx.cfg());
        String limited = sql.trim();
        if (!limited.toUpperCase().contains(" LIMIT ")) {
            limited = limited + " LIMIT " + MAX_ROWS;
        }
        try {
            Class.forName(ctx.driver());
            try (Connection connection = DriverManager.getConnection(ctx.url(), ctx.username(), ctx.password());
                    Statement statement = connection.createStatement();
                    ResultSet rs = statement.executeQuery(limited)) {
                int columnCount = columnList.size();
                List<String[]> rows = new ArrayList<>();
                while (rs.next()) {
                    String[] row = new String[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        Object v = rs.getObject(i);
                        row[i - 1] = v == null ? null : String.valueOf(v);
                    }
                    rows.add(row);
                }
                return new AggregateResultDto(columnList, rows);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("집계 쿼리 실패: " + e.getMessage(), e);
        }
    }

    private static List<AggregateColumnDto> buildColumnList(AggCfg cfg) {
        List<AggregateColumnDto> columnList = new ArrayList<>();
        int idx = 0;
        for (DimCfg d : cfg.rows()) {
            if (!validName(d.columnName())) {
                continue;
            }
            columnList.add(new AggregateColumnDto(idx++, null, d.columnName()));
        }
        for (DimCfg d : cfg.columns()) {
            if (!validName(d.columnName())) {
                continue;
            }
            columnList.add(new AggregateColumnDto(idx++, null, d.columnName()));
        }
        for (ValueCfg v : cfg.values()) {
            if (!validName(v.column())) {
                continue;
            }
            columnList.add(new AggregateColumnDto(idx++, v.aggType(), v.column()));
        }
        return columnList;
    }

    private static boolean validName(String name) {
        return name != null && !name.isBlank();
    }

    private String buildAggregateSql(QueryContext ctx, boolean forView) {
        AggCfg cfg = ctx.cfg();
        Set<String> dimCols = new LinkedHashSet<>();
        cfg.rows().stream().map(DimCfg::columnName).filter(JdbcAggregateQueryAdapter::validName).forEach(dimCols::add);
        cfg.columns().stream().map(DimCfg::columnName).filter(JdbcAggregateQueryAdapter::validName).forEach(dimCols::add);

        List<String> selectParts = new ArrayList<>();
        for (String col : dimCols) {
            selectParts.add(quote(col));
        }
        for (ValueCfg v : cfg.values()) {
            if (!validName(v.column())) {
                continue;
            }
            selectParts.add(aggExpr(v) + " AS " + quote(v.aggType() + "_" + v.column()));
        }
        if (selectParts.isEmpty()) {
            throw new IllegalArgumentException(
                    "집계할 컬럼이 없습니다. Value 에 Measure 를 추가하거나 Row/Column 을 확인하세요.");
        }

        String from = "(\n" + ctx.datasetSql() + "\n) cb_view";
        String where = buildWhere(cfg);
        String groupBy = dimCols.isEmpty() ? "" : "GROUP BY " + dimCols.stream().map(JdbcAggregateQueryAdapter::quote).collect(Collectors.joining(", "));

        return "SELECT " + String.join(", ", selectParts) + "\nFROM " + from + "\n" + where + "\n" + groupBy;
    }

    private static String aggExpr(ValueCfg v) {
        String col = quote(v.column());
        return switch (v.aggType().toLowerCase()) {
            case "sum" -> "SUM(" + col + ")";
            case "avg" -> "AVG(" + col + ")";
            case "max" -> "MAX(" + col + ")";
            case "min" -> "MIN(" + col + ")";
            case "distinct" -> "COUNT(DISTINCT " + col + ")";
            default -> "COUNT(" + col + ")";
        };
    }

    private static String buildWhere(AggCfg cfg) {
        List<String> clauses = new ArrayList<>();
        Stream.of(cfg.rows(), cfg.columns(), cfg.filters())
                .flatMap(List::stream)
                .forEach(d -> {
                    String c = filterClause(d);
                    if (c != null) {
                        clauses.add(c);
                    }
                });
        if (clauses.isEmpty()) {
            return "";
        }
        return "WHERE " + String.join(" AND ", clauses);
    }

    private static String filterClause(DimCfg d) {
        if (!validName(d.columnName()) || d.values() == null || d.values().isEmpty()) {
            return null;
        }
        String col = quote(d.columnName());
        String type = d.filterType() == null ? "=" : d.filterType();
        return switch (type) {
            case "=", "eq" -> col + " IN (" + quoteList(d.values()) + ")";
            case "≠", "ne" -> col + " NOT IN (" + quoteList(d.values()) + ")";
            case ">" -> col + " > " + quoteValue(d.values().get(0));
            case "<" -> col + " < " + quoteValue(d.values().get(0));
            default -> col + " IN (" + quoteList(d.values()) + ")";
        };
    }

    private static String quoteList(List<String> values) {
        return values.stream().map(JdbcAggregateQueryAdapter::quoteValue).collect(Collectors.joining(", "));
    }

    private static String quoteValue(String v) {
        if (v == null) {
            return "NULL";
        }
        try {
            Double.parseDouble(v);
            return v;
        } catch (NumberFormatException e) {
            return "'" + v.replace("'", "''") + "'";
        }
    }

    /** H2 local profile uses DATABASE_TO_LOWER=TRUE — 식별자를 소문자로 맞춤 */
    private static String quote(String identifier) {
        return identifier.toLowerCase();
    }

    private static AggCfg parseCfg(String configJson) {
        if (configJson == null || configJson.isBlank()) {
            return new AggCfg(List.of(), List.of(), List.of(), List.of());
        }
        try {
            return JsonMapper.mapper().readValue(configJson, AggCfg.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("집계 cfg 파싱 실패", e);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AggCfg(List<DimCfg> rows, List<DimCfg> columns, List<DimCfg> filters, List<ValueCfg> values) {
        AggCfg {
            rows = rows != null ? rows : List.of();
            columns = columns != null ? columns : List.of();
            filters = filters != null ? filters : List.of();
            values = values != null ? values : List.of();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record DimCfg(String columnName, String filterType, List<String> values) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ValueCfg(String column, String aggType) {
        ValueCfg {
            aggType = aggType != null ? aggType : "sum";
        }
    }

    private record QueryContext(
            String driver, String url, String username, String password, String datasetSql, AggCfg cfg) {}
}
