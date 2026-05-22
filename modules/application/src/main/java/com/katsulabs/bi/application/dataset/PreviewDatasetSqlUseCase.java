package com.katsulabs.bi.application.dataset;

public class PreviewDatasetSqlUseCase {

    private final DatasetSqlPreviewPort datasetSqlPreviewPort;

    public PreviewDatasetSqlUseCase(DatasetSqlPreviewPort datasetSqlPreviewPort) {
        this.datasetSqlPreviewPort = datasetSqlPreviewPort;
    }

    public DatasetPreviewResult execute(long datasourceId, String sql, int maxRows) {
        int limit = Math.min(Math.max(maxRows, 1), 500);
        return datasetSqlPreviewPort.previewByDatasource(datasourceId, sql, limit);
    }
}
