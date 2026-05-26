package com.katsulabs.bi.application.domains.dataset;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.domain.domains.dataset.DatasetRepository;

@RequiredArgsConstructor
public class DeleteDatasetUseCase {

    private final DatasetRepository datasetRepository;


    public ServiceResult execute(long id) {
        if (datasetRepository.findById(id).isEmpty()) {
            return ServiceResult.fail("데이터셋을 찾을 수 없습니다.");
        }
        datasetRepository.delete(id);
        return ServiceResult.success("success");
    }
}
