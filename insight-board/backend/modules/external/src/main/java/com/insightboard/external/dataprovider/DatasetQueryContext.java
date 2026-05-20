package com.insightboard.external.dataprovider;

/** Resolved dataset query: JDBC source + table or SQL. */
public record DatasetQueryContext(Long datasourceId, String fromClause) {}
