package com.bdp.application.cboard.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DimensionConfigDto {
    private String columnName;
    private String filterType;
    private List<String> values;
    private String id;
    private String custom;
}
