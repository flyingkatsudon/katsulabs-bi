package com.bdp.application.cboard.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueConfigDto {
    private String column;
    private String aggType;
}
