package com.katsulabs.insightboard.application.dataset;

import com.katsulabs.insightboard.application.common.ServiceResult;
import com.katsulabs.insightboard.domain.dataset.DatasetRepository;

public class DeleteDatasetUseCase {

    private final DatasetRepository datasetRepository;

    public DeleteDatasetUseCase(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    public ServiceResult execute(long id) {
        if (datasetRepository.findById(id).isEmpty()) {
            return ServiceResult.fail("데이터셋을 찾을 수 없습니다.");
        }
        datasetRepository.delete(id);
        return ServiceResult.success("success");
    }
}
