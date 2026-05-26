package com.katsulabs.bi.application.domains.dataset;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.domain.domains.dataset.DatasetDetail;
import com.katsulabs.bi.domain.domains.dataset.DatasetRepository;

@RequiredArgsConstructor
public class GetDatasetUseCase {

    private final DatasetRepository datasetRepository;


    public DatasetDetail execute(long id) {
        return datasetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("데이터셋을 찾을 수 없습니다: " + id));
    }
}
