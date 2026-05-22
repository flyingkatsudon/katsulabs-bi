package com.katsulabs.bi.application.aggregate;

import java.util.List;

public record AggregateResultDto(List<AggregateColumnDto> columnList, List<String[]> data) {}
