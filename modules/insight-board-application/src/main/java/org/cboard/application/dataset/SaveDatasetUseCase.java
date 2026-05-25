package org.cboard.application.dataset;

import org.cboard.application.common.ServiceResult;
import org.cboard.domain.dataset.DatasetRepository;

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
