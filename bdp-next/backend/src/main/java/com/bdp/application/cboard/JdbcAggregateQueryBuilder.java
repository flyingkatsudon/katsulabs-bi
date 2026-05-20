package com.bdp.application.cboard;

import com.bdp.application.cboard.config.AggConfigDto;
import com.bdp.application.cboard.config.DimensionConfigDto;
import com.bdp.application.cboard.config.ValueConfigDto;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class JdbcAggregateQueryBuilder {

    private static final Pattern SAFE_IDENT = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");

    public record BuiltQuery(String sql, List<String> selectAliases, List<String> groupByColumns) {}

    public BuiltQuery build(String baseTableOrSql, AggConfigDto cfg) {
        String fromClause = resolveFrom(baseTableOrSql);
        List<DimensionConfigDto> dims = new ArrayList<>();
        if (cfg.getRows() != null) {
            dims.addAll(cfg.getRows());
        }
        if (cfg.getColumns() != null) {
            dims.addAll(cfg.getColumns());
        }

        List<String> selectParts = new ArrayList<>();
        List<String> groupBy = new ArrayList<>();
        List<String> aliases = new ArrayList<>();

        for (DimensionConfigDto d : dims) {
            String col = requireSafe(d.getColumnName());
            selectParts.add(col);
            groupBy.add(col);
            aliases.add(col);
        }

        List<ValueConfigDto> values = cfg.getValues() != null ? cfg.getValues() : List.of();
        if (values.isEmpty() && groupBy.isEmpty()) {
            selectParts.add("COUNT(*) AS cnt");
            aliases.add("cnt");
        } else {
            for (ValueConfigDto v : values) {
                String col = requireSafe(v.getColumn());
                String agg = normalizeAgg(v.getAggType());
                String alias = agg.toLowerCase() + "_" + col;
                selectParts.add(agg + "(" + col + ") AS " + alias);
                aliases.add(alias);
            }
        }

        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append(String.join(", ", selectParts));
        sql.append(" FROM ").append(fromClause);

        List<String> where = buildWhere(cfg);
        if (!where.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", where));
        }
        if (!groupBy.isEmpty()) {
            sql.append(" GROUP BY ").append(String.join(", ", groupBy));
        }
        sql.append(" ORDER BY 1");
        return new BuiltQuery(sql.toString(), aliases, groupBy);
    }

    public String buildDistinctQuery(String baseTableOrSql, String columnName) {
        String from = resolveFrom(baseTableOrSql);
        String col = requireSafe(columnName);
        return "SELECT DISTINCT " + col + " FROM " + from + " ORDER BY 1";
    }

    public String buildPreviewColumnsQuery(String baseTableOrSql) {
        return "SELECT * FROM " + resolveFrom(baseTableOrSql) + " FETCH FIRST 100 ROWS ONLY";
    }

    private List<String> buildWhere(AggConfigDto cfg) {
        List<String> clauses = new ArrayList<>();
        if (cfg.getFilters() == null) {
            return clauses;
        }
        for (DimensionConfigDto f : cfg.getFilters()) {
            if (f.getColumnName() == null || f.getValues() == null || f.getValues().isEmpty()) {
                continue;
            }
            String col = requireSafe(f.getColumnName());
            String type = f.getFilterType() != null ? f.getFilterType() : "=";
            if ("≥".equals(type) || ">=".equals(type)) {
                clauses.add(col + " >= '" + escape(f.getValues().get(0)) + "'");
            } else if ("≤".equals(type) || "<=".equals(type)) {
                clauses.add(col + " <= '" + escape(f.getValues().get(0)) + "'");
            } else if ("≠".equals(type) || "!=".equals(type)) {
                clauses.add(col + " <> '" + escape(f.getValues().get(0)) + "'");
            } else if (f.getValues().size() > 1 || "∈".equals(type)) {
                clauses.add(col + " IN (" + inList(f.getValues()) + ")");
            } else {
                clauses.add(col + " = '" + escape(f.getValues().get(0)) + "'");
            }
        }
        return clauses;
    }

    private static String resolveFrom(String baseTableOrSql) {
        String trimmed = baseTableOrSql.trim();
        if (trimmed.regionMatches(true, 0, "SELECT", 0, 6)) {
            return "(" + trimmed + ") t";
        }
        return requireSafe(trimmed);
    }

    private static String normalizeAgg(String aggType) {
        if (aggType == null || aggType.isBlank()) {
            return "SUM";
        }
        return switch (aggType.toLowerCase()) {
            case "count" -> "COUNT";
            case "avg", "average" -> "AVG";
            case "max" -> "MAX";
            case "min" -> "MIN";
            default -> "SUM";
        };
    }

    private static String requireSafe(String ident) {
        if (ident == null || !SAFE_IDENT.matcher(ident).matches()) {
            throw new IllegalArgumentException("Invalid column or table: " + ident);
        }
        return ident;
    }

    private static String escape(String v) {
        return v == null ? "" : v.replace("'", "''");
    }

    private static String inList(List<String> values) {
        Set<String> unique = new LinkedHashSet<>(values);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String v : unique) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("'").append(escape(v)).append("'");
            first = false;
        }
        return sb.toString();
    }
}
