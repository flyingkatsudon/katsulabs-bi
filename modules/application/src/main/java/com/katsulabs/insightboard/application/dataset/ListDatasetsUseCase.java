package com.katsulabs.insightboard.application.dataset;

import java.util.List;

import com.katsulabs.insightboard.domain.dataset.DatasetRepository;
import com.katsulabs.insightboard.domain.dataset.DatasetSummary;

public class ListDatasetsUseCase {

    private final DatasetRepository datasetRepository;

    public ListDatasetsUseCase(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    public List<DatasetSummary> execute() {
        return datasetRepository.findAllSummaries();
    }
}
