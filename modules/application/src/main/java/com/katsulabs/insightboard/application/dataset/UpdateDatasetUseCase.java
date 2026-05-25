package com.katsulabs.insightboard.application.dataset;

import com.katsulabs.insightboard.application.common.ServiceResult;
import com.katsulabs.insightboard.domain.dataset.DatasetRepository;

public class UpdateDatasetUseCase {

    private final DatasetRepository datasetRepository;

    public UpdateDatasetUseCase(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    public ServiceResult execute(String userId, long id, DatasetWriteCommand command) {
        if (datasetRepository.findById(id).isEmpty()) {
            return ServiceResult.fail("데이터셋을 찾을 수 없습니다.");
        }
        if (datasetRepository.existsByName(userId, command.name(), command.categoryName(), id)) {
            return ServiceResult.fail("Duplicated name");
        }
        datasetRepository.update(id, command.name(), command.categoryName(), command.dataJson());
        return ServiceResult.success("success");
    }
}
