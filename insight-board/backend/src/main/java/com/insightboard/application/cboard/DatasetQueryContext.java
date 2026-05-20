package com.insightboard.application.cboard;

/** Resolved dataset query: JDBC source + table or SQL. */
public record DatasetQueryContext(Long datasourceId, String fromClause) {}
