package org.cboard.application.dataset;

import java.util.List;

import org.cboard.domain.dataset.DatasetRepository;
import org.cboard.domain.dataset.DatasetSummary;

public class ListDatasetsUseCase {

    private final DatasetRepository datasetRepository;

    public ListDatasetsUseCase(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    public List<DatasetSummary> execute() {
        return datasetRepository.findAllSummaries();
    }
}
