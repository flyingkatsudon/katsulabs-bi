package com.katsulabs.bi.adapter.web.dataset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatasetSqlPreviewRequest(
        @NotNull Long datasourceId, @NotBlank String sql, Integer limit) {}
