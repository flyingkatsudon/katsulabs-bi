package com.katsulabs.insightboard.application.aggregate;

import java.util.List;

public record AggregateResultDto(List<AggregateColumnDto> columnList, List<String[]> data) {}
