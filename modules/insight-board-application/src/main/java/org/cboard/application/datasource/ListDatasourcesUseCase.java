package org.cboard.application.datasource;

import java.util.List;

import org.cboard.domain.datasource.DatasourceRepository;
import org.cboard.domain.datasource.DatasourceSummary;

public class ListDatasourcesUseCase {

    private final DatasourceRepository datasourceRepository;

    public ListDatasourcesUseCase(DatasourceRepository datasourceRepository) {
        this.datasourceRepository = datasourceRepository;
    }

    public List<DatasourceSummary> execute() {
        return datasourceRepository.findAllSummaries();
    }
}
