package com.katsulabs.bi.application.dataset;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.domain.dataset.DatasetDetail;
import com.katsulabs.bi.domain.dataset.DatasetRepository;

@RequiredArgsConstructor
public class GetDatasetUseCase {

    private final DatasetRepository datasetRepository;


    public DatasetDetail execute(long id) {
        return datasetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("데이터셋을 찾을 수 없습니다: " + id));
    }
}
