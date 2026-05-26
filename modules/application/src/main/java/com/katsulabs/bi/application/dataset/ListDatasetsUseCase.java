package com.katsulabs.bi.application.dataset;

import lombok.RequiredArgsConstructor;

import java.util.List;

import com.katsulabs.bi.domain.dataset.DatasetRepository;
import com.katsulabs.bi.domain.dataset.DatasetSummary;

@RequiredArgsConstructor
public class ListDatasetsUseCase {

    private final DatasetRepository datasetRepository;


    public List<DatasetSummary> execute() {
        return datasetRepository.findAllSummaries();
    }
}
