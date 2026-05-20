package com.insightboard.api.application.cboard.dto;

import java.util.List;

/** CBoard {@code AggregateResult} JSON 호환. */
public record AggregateResultDto(List<ColumnIndexDto> columnList, String[][] data) {}
