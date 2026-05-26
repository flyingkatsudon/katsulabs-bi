package com.katsulabs.bi.application.domains.dataset;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PreviewDatasetUseCase {

    private final DatasetSqlPreviewPort datasetSqlPreviewPort;


    public DatasetPreviewResult execute(long datasetId, int maxRows) {
        int limit = Math.min(Math.max(maxRows, 1), 500);
        return datasetSqlPreviewPort.preview(datasetId, limit);
    }
}
