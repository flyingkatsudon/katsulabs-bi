package com.bdp.application.cboard.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/** 레거시 CBoard {@code ViewAggConfig} JSON 호환. */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ViewAggConfigDto {
    private List<DimensionConfigDto> rows;
    private List<DimensionConfigDto> columns;
    private List<DimensionConfigDto> filters;
    private List<ValueConfigDto> values;

    public AggConfigDto toAggConfig() {
        AggConfigDto agg = new AggConfigDto();
        agg.setRows(rows != null ? rows : List.of());
        agg.setColumns(columns != null ? columns : List.of());
        agg.setFilters(filters != null ? new ArrayList<>(filters) : new ArrayList<>());
        agg.setValues(values != null ? values : List.of());
        return agg;
    }
}
