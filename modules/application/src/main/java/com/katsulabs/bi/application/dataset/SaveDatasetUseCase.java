package com.katsulabs.bi.application.dataset;

import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.domain.dataset.DatasetRepository;

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
