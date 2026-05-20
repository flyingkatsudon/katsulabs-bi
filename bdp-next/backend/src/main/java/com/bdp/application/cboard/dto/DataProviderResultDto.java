package com.bdp.application.cboard.dto;

/** CBoard {@code DataProviderResult} JSON 호환. */
public record DataProviderResultDto(String[][] data, String[] columns, String msg, int resultCount) {}
