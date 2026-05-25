package com.katsulabs.insightboard.application.dataset;

import com.katsulabs.insightboard.domain.dataset.DatasetDetail;
import com.katsulabs.insightboard.domain.dataset.DatasetRepository;

public class GetDatasetUseCase {

    private final DatasetRepository datasetRepository;

    public GetDatasetUseCase(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    public DatasetDetail execute(long id) {
        return datasetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("데이터셋을 찾을 수 없습니다: " + id));
    }
}
