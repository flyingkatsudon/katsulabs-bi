package com.katsulabs.bi.application.domains.aggregate;

import java.util.List;

public record AggregateResultDto(List<AggregateColumnDto> columnList, List<String[]> data) {}
