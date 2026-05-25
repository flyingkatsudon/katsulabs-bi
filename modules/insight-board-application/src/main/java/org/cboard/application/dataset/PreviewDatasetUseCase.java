package org.cboard.application.dataset;

public class PreviewDatasetUseCase {

    private final DatasetSqlPreviewPort datasetSqlPreviewPort;

    public PreviewDatasetUseCase(DatasetSqlPreviewPort datasetSqlPreviewPort) {
        this.datasetSqlPreviewPort = datasetSqlPreviewPort;
    }

    public DatasetPreviewResult execute(long datasetId, int maxRows) {
        int limit = Math.min(Math.max(maxRows, 1), 500);
        return datasetSqlPreviewPort.preview(datasetId, limit);
    }
}
