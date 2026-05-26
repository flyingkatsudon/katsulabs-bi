package com.katsulabs.bi.application.dataset;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.domain.dataset.DatasetRepository;

@RequiredArgsConstructor
public class UpdateDatasetUseCase {

    private final DatasetRepository datasetRepository;


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
