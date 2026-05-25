package com.katsulabs.insightboard.application.datasource;

import java.util.List;

import com.katsulabs.insightboard.domain.datasource.DatasourceRepository;
import com.katsulabs.insightboard.domain.datasource.DatasourceSummary;

public class ListDatasourcesUseCase {

    private final DatasourceRepository datasourceRepository;

    public ListDatasourcesUseCase(DatasourceRepository datasourceRepository) {
        this.datasourceRepository = datasourceRepository;
    }

    public List<DatasourceSummary> execute() {
        return datasourceRepository.findAllSummaries();
    }
}
