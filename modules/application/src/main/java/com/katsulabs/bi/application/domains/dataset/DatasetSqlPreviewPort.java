package com.katsulabs.bi.application.domains.dataset;

public interface DatasetSqlPreviewPort {

    DatasetPreviewResult preview(long datasetId, int maxRows);

    DatasetPreviewResult previewByDatasource(long datasourceId, String sql, int maxRows);
}
