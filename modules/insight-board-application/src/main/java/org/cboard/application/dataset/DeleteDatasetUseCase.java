package org.cboard.application.dataset;

import org.cboard.application.common.ServiceResult;
import org.cboard.domain.dataset.DatasetRepository;

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
