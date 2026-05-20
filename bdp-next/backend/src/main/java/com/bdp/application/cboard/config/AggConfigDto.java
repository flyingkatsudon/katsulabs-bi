package com.bdp.application.cboard.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class AggConfigDto {
    private List<DimensionConfigDto> rows = new ArrayList<>();
    private List<DimensionConfigDto> columns = new ArrayList<>();
    private List<DimensionConfigDto> filters = new ArrayList<>();
    private List<ValueConfigDto> values = new ArrayList<>();
}
