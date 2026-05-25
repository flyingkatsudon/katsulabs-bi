package com.katsulabs.insightboard.application.dataset;

import com.katsulabs.insightboard.application.common.ServiceResult;
import com.katsulabs.insightboard.domain.dataset.DatasetRepository;

public class SaveDatasetUseCase {

    private final DatasetRepository datasetRepository;

    public SaveDatasetUseCase(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    public ServiceResult execute(String userId, DatasetWriteCommand command) {
        if (datasetRepository.existsByName(userId, command.name(), command.categoryName(), null)) {
            return ServiceResult.fail("Duplicated name");
        }
        long id = datasetRepository.insert(userId, command.name(), command.categoryName(), command.dataJson());
        return ServiceResult.success("success", id);
    }
}
