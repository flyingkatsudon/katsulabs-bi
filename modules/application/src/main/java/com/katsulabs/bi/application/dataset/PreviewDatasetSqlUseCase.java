package com.katsulabs.bi.application.dataset;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PreviewDatasetSqlUseCase {

    private final DatasetSqlPreviewPort datasetSqlPreviewPort;


    public DatasetPreviewResult execute(long datasourceId, String sql, int maxRows) {
        int limit = Math.min(Math.max(maxRows, 1), 500);
        return datasetSqlPreviewPort.previewByDatasource(datasourceId, sql, limit);
    }
}
